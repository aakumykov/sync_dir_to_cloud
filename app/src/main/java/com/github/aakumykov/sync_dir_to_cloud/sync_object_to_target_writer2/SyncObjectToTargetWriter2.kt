package com.github.aakumykov.sync_dir_to_cloud.sync_object_to_target_writer2

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.utils.CountingBufferedInputStream
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.progress_info_holder.ProgressInfoHolder
import com.github.aakumykov.sync_dir_to_cloud.storage_writer2.StorageWriter2
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.InputStreamSupplier
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Задача класса - записывать данные, на который указывает SyncObject,
 * по назначению и менять статус объекта в БД.
 */
class SyncObjectToTargetWriter2 @AssistedInject constructor(
    @Assisted private val storageWriter2: StorageWriter2,
    private val inputStreamSupplierCreator: InputStreamSupplier.Creator,
    private val progressInfoHolder: ProgressInfoHolder,
    private val syncObjectStateChanger: SyncObjectStateChanger,
){
    // TODO: передавать не SyncTask, а его часть.
    suspend fun write(syncTask: SyncTask, syncObject: SyncObject, overwriteIfExists: Boolean = false) {

        syncObjectStateChanger.changeSyncState(syncObject.id, ExecutionState.RUNNING)

        writeReal(syncTask, syncObject, overwriteIfExists)
            .onSuccess {
                syncObjectStateChanger.markAsSuccessfullySynced(syncObject.id)
            }
            .onFailure {
                syncObjectStateChanger.changeSyncState(
                    syncObject.id,
                    ExecutionState.ERROR,
                    ExceptionUtils.getErrorMessage(it)
                )
            }
    }


    /**
     * @return Полное имя (абсолютный путь) к файлу.
     */
    private suspend fun writeReal(syncTask: SyncTask, syncObject: SyncObject, overwriteIfExists: Boolean): Result<String> {
        return if (syncObject.isDir) createDirReal(syncTask, syncObject)
        else putFileReal(syncTask, syncObject, overwriteIfExists)
    }


    private suspend fun putFileReal(
        syncTask: SyncTask,
        syncObject: SyncObject,
        overwriteIfExists: Boolean
    ): Result<String> {
        return try {
            inputStreamSupplier(syncTask)
                .getInputStreamFor(syncObject.absolutePathIn(syncTask.sourcePath!!))!!
                .getOrThrow()
                .use { inputStream ->

                    progressInfoHolder.addProgressInfo(syncObject.toProgressInfo())

                    val countingInputStream = CountingBufferedInputStream(inputStream) { count ->
                        Log.d(
                            "CountingInputStream",
                            "Файл '${syncObject.name}'" +
                                    ", размер: ${syncObject.size}" +
                                    ", новый размер: ${syncObject.newSize}" +
                                    ", обработано: $count"
                        )
                        progressInfoHolder.setProgress(syncObject.id, count)
                    }

                    storageWriter2.putFile(
                        countingInputStream,
                        syncObject.absolutePathIn(syncTask.targetPath!!),
                        overwriteIfExists
                    )
                }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private suspend fun createDirReal(syncTask: SyncTask, syncObject: SyncObject): Result<String> {
        val basePath = syncTask.targetPath + "/" + syncObject.relativeParentDirPath
        val fileName = syncObject.name
        return storageWriter2.createDir(basePath, fileName)
    }


    private var _inputStreamSupplier: InputStreamSupplier? = null

    private suspend fun inputStreamSupplier(syncTask: SyncTask): InputStreamSupplier {
        if (null == _inputStreamSupplier)
            _inputStreamSupplier = inputStreamSupplierCreator.create(syncTask)
        return _inputStreamSupplier!!
    }
}


private fun SyncObject.toProgressInfo(): ProgressInfoHolder.ProgressInfo {
    return ProgressInfoHolder.ProgressInfo(
        isDir,
        id,
        (newSize ?: size),
        0L
    )
}
