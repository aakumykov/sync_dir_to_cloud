package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
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
    private var inputStreamSupplier: InputStreamSupplier? = null

    // TODO: передавать не SyncTask, а его часть.

    suspend fun write(syncTask: SyncTask, list: List<SyncObject>, overwriteIfExists: Boolean = false) {
        inputStreamSupplier = inputStreamSupplierCreator.create(syncTask)
        list.forEach { syncObject ->
            write(syncTask, syncObject, overwriteIfExists)
        }
    }

    suspend fun write(syncTask: SyncTask, syncObject: SyncObject, overwriteIfExists: Boolean = false) {
        try {
            syncObjectStateChanger.changeExecutionState(syncObject.id, ExecutionState.RUNNING)
            writeReal(syncTask, syncObject, overwriteIfExists)
            syncObjectStateChanger.changeExecutionState(syncObject.id, ExecutionState.SUCCESS)
        }
        catch (e: Exception) {
            syncObjectStateChanger.changeExecutionState(syncObject.id, ExecutionState.ERROR, ExceptionUtils.getErrorMessage(e))
            Log.e(tag, ExceptionUtils.getErrorMessage(e), e);
        }
    }


    @Throws(Exception::class)
    private suspend fun writeReal(syncTask: SyncTask, syncObject: SyncObject, overwriteIfExists: Boolean) {

        val path = syncTask.targetPath + "/" + syncObject.relativeParentDirPath

        if (syncObject.isDir) {
            storageWriter2.createDir(path, syncObject.name)
        }
        else {
            // Используются операторы "!!", чтобы вызывать и фиксировать ошибку.
            storageWriter2.putFile(
                inputStreamSupplier!!.getInputStreamFor(path)!!.getOrThrow(),
                syncTask.targetPath!!,
                overwriteIfExists
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(storageWriter2: StorageWriter2?): SyncObjectToTargetWriter2?
    }

    // FIXME: не нравится мне то, что часто нужно передавать cloudAuthReader
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