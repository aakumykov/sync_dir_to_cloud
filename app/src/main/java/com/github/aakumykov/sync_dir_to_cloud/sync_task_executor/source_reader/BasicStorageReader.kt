package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader

import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.di.factories.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.interfaces.StorageReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.utils.calculateRelativeParentDirPath

abstract class BasicStorageReader(
    private val taskId: String,
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,
    private val changesDetectionStrategy: ChangesDetectionStrategy,
    private val authToken: String,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectAdder: SyncObjectAdder,
    private val syncObjectUpdater: SyncObjectUpdater
)
    : StorageReader
{
    protected abstract val storageType: StorageType


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