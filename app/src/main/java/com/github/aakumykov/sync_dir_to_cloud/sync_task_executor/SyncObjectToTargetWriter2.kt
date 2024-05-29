package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.tag
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.storage_writer2.StorageWriter2
import com.github.aakumykov.sync_dir_to_cloud.storage_writer2.StorageWriter2_Creator
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

/**
 * Задача класса - записывать данные, на который указывает SyncObject, по назначению
 * и менять статус объекта в БД.
 */
class SyncObjectToTargetWriter2 @AssistedInject constructor(
    private val inputStreamSupplierCreator: InputStreamSupplier.Creator,
    @Assisted private val storageWriter2: StorageWriter2,
    private val syncObjectStateChanger: SyncObjectStateChanger,
){
    // TODO: передавать не SyncTask, а его часть.
    suspend fun write(syncTask: SyncTask, syncObject: SyncObject, overwriteIfExists: Boolean = false) {

        syncObjectStateChanger.changeExecutionState(syncObject.id, ExecutionState.RUNNING)

        writeReal(syncTask, syncObject, overwriteIfExists)
            .onSuccess {
                syncObjectStateChanger.changeExecutionState(syncObject.id, ExecutionState.SUCCESS)
            }
            .onFailure {
                syncObjectStateChanger.changeExecutionState(syncObject.id, ExecutionState.ERROR, ExceptionUtils.getErrorMessage(it))
            }
    }

    /**
     * @return Полное имя (абсолютный путь) к файлу.
     */
    private suspend fun writeReal(syncTask: SyncTask, syncObject: SyncObject, overwriteIfExists: Boolean): Result<String> {
        return if (syncObject.isDir) createDirReal(syncTask, syncObject)
        else putFileReal(syncTask, syncObject, overwriteIfExists)
    }

    private suspend fun putFileReal(syncTask: SyncTask, syncObject: SyncObject, overwriteIfExists: Boolean): Result<String> {
        return try {
            inputStreamSupplier(syncTask)
                .getInputStreamFor(syncObject.absolutePathIn(syncTask.sourcePath!!))!!
                .getOrThrow()
                .use { inputStream ->
                    storageWriter2.putFile(
                        inputStream,
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



    @AssistedFactory
    interface Factory {
        fun create(storageWriter2: StorageWriter2?): SyncObjectToTargetWriter2?
    }


    // FIXME: не нравится мне то, что нужно часто передавать cloudAuthReader
    class Creator @Inject constructor(
        private val cloudAuthReader: CloudAuthReader,
        private val storageWriter2Creator: StorageWriter2_Creator,
        private val syncObjectToTargetWriterFactory: Factory
    ){
        suspend fun create(syncTask: SyncTask): SyncObjectToTargetWriter2? {
            return syncObjectToTargetWriterFactory.create(
                storageWriter2Creator.createStorageWriter(
                    syncTask.targetStorageType,
                    cloudAuthReader.getCloudAuth(syncTask.targetAuthId)?.authToken
                )
            )
        }
    }
}