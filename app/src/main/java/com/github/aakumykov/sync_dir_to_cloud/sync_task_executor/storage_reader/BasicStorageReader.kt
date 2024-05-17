package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader

import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.FileListerFactoryCreator
import com.github.aakumykov.sync_dir_to_cloud.di.factories.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.interfaces.StorageReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.utils.calculateRelativeParentDirPath

abstract class BasicStorageReader(
    private val taskId: String,
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,
    private val fileListerFactoryCreator: FileListerFactoryCreator,
    private val changesDetectionStrategy: ChangesDetectionStrategy,
    private val authToken: String,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectAdder: SyncObjectAdder,
    private val syncObjectUpdater: SyncObjectUpdater,
    private val syncObjectStateChanger: SyncObjectStateChanger
)
    : StorageReader
{
    protected abstract val storageType: StorageType


    override suspend fun checkDbObjectsForExistenceAtStorage(targetDirPath: String) {

        fileListerFactoryCreator.createFileListerFactory(storageType)?.createFileLister(authToken).also { fileLister ->

            syncObjectReader.getObjectsForTask(taskId).forEach { syncObject ->

                val modificationState = try {
                    fileLister?.fileExists(syncObject.absolutePathIn(targetDirPath))?.let {
                        if (it.getOrThrow())
                            ModificationState.UNKNOWN
                        else
                            ModificationState.DELETED
                    } ?: ModificationState.UNKNOWN
                }
                catch (t: Throwable) {
                    ModificationState.UNKNOWN
                }

                syncObjectStateChanger.changeSourceModificationState(syncObject.id, modificationState)
            }
        }
    }


    private val recursiveDirReader: RecursiveDirReader?
        get() = recursiveDirReaderFactory.create(storageType, authToken)


    @Throws(FileLister.NotADirException::class)
    override suspend fun read(sourcePath: String) {

        recursiveDirReader?.getRecursiveList(
            path = sourcePath,
            sortingMode = SimpleSortingMode.NAME,
            reverseOrder = false,
            foldersFirst = true,
            dirMode = false
        )?.forEach { fileListItem: RecursiveDirReader.FileListItem ->

            val existingObject = syncObjectReader.getSyncObject(taskId, fileListItem.name)

            if (null == existingObject) {
                SyncObject.createAsNew(
                    taskId = taskId,
                    fsItem = fileListItem,
                    relativeParentDirPath = calculateRelativeParentDirPath(fileListItem, sourcePath),
                )
                    .also {
                        syncObjectAdder.addSyncObject(it)
                    }
            }
            else {
                SyncObject.createAsExisting(
                    existingObject,
                    fileListItem,
                    changesDetectionStrategy.detectItemModification(sourcePath, fileListItem, existingObject)
                )
                    .also {
                        syncObjectUpdater.updateSyncObject(it)
                    }
            }
        }
    }
}