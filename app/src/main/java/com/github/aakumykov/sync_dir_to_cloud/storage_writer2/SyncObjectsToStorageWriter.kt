package com.github.aakumykov.sync_dir_to_cloud.storage_writer2

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator.SourceFileStreamSupplierCreator
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class SyncObjectsToStorageWriter @AssistedInject constructor(
    @Assisted authToken: String,
    @Assisted targetStorageType: StorageType,
    private val sourceFileStreamSupplierCreator: SourceFileStreamSupplierCreator,
    private val storageWriter2Creator: StorageWriter2_Creator,
    private val syncObjectStateChanger: SyncObjectStateChanger
) {
    private var sourceFileStreamSupplier: SourceFileStreamSupplier? = null

    private val storageWriter2: StorageWriter2?
        get() = storageWriter2Creator.createStorageWriter(StorageType.LOCAL,"")


    // TODO: передавать урезанный объект/интерфейс с нужной информацией вместо SyncTask
    suspend fun writeObjectsToTarget(objectListToSync: List<SyncObject>, syncTask: SyncTask) {

        /*prepareWorkStuff(syncTask)

        // Обработка каталогов
        objectListToSync
            .filter { it.isDir }
            .forEach { dirSyncObject ->
            processSyncObject(dirSyncObject) {
                storageWriter2?.createDir(dirSyncObject.absolutePathIn(syncTask.targetPath!!),)
            }
        }

        // Обработка каталогов
        objectListToSync
            .filter { !it.isDir }
            .forEach { fileSyncObject ->
                processSyncObject(fileSyncObject) {
                    sourceFileStreamSupplier
                        ?.getSourceFileStream(fileSyncObject.absolutePathIn(syncTask.sourcePath!!))
                        ?.getOrThrow().also { inputStream ->
                            inputStream.use {
                                storageWriter2?.putFile(it, syncTask.targetPath!!, true)
                            }
                        }
                }
            }*/
    }


    private fun prepareWorkStuff(syncTask: SyncTask) {

        /*if (null == sourceFileStreamSupplier)
            sourceFileStreamSupplier = sourceFileStreamSupplierCreator.create(syncTask.id, syncTask.sourceStorageType)

        if (null == storageWriter2) {
            storageWriter2 = storageWriter2Creator.createStorageWriter(
                syncTask.targetStorageType,
                syncTask.targetAuthId
            )
        }*/
    }


    private suspend fun processSyncObject(syncObject: SyncObject, block: SuspendableRunnable) {
        try {
            with(syncObject) {
                syncObjectStateChanger.changeExecutionState(id, com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState.RUNNING)
                block.runSuspend()
                syncObjectStateChanger.changeExecutionState(id, com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState.SUCCESS)
            }
        } catch (e: Exception) {
            syncObjectStateChanger.changeExecutionState(syncObject.id, ExecutionState.ERROR, ExceptionUtils.getErrorMessage(e))
        }
    }


    private fun interface SuspendableRunnable {
        suspend fun runSuspend()
    }

    @AssistedFactory
    interface Factory {
        fun create(authToken: String?, targetStorageType: StorageType?): SyncObjectsToStorageWriter
    }

    class Creator @Inject constructor(private val factory: Factory) {
        fun create(authToken: String?, targetStorageType: StorageType?): SyncObjectsToStorageWriter {
            return factory.create(authToken, targetStorageType)
        }
    }
}