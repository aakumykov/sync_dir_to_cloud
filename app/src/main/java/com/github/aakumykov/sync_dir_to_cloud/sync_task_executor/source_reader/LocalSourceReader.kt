package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader

import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.AssistedArgName
import com.github.aakumykov.sync_dir_to_cloud.di.factories.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.interfaces.SourceReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.interfaces.SourceReaderAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.utils.calculateRelativeParentDirPath
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class LocalSourceReader @AssistedInject constructor(
    @Assisted(AssistedArgName.AUTH_TOKEN) private val authToken: String,
    @Assisted(AssistedArgName.TASK_ID) private val taskId: String,
    @Assisted private val changesDetectionStrategy: ChangesDetectionStrategy,
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectAdder: SyncObjectAdder,
    private val syncObjectUpdater: SyncObjectUpdater
)
    : SourceReader
{
    @AssistedFactory
    interface Factory : SourceReaderAssistedFactory {
        override fun create(@Assisted(AssistedArgName.AUTH_TOKEN) authToken: String,
                            @Assisted(AssistedArgName.TASK_ID) taskId: String,
                            @Assisted changesDetectionStrategy: ChangesDetectionStrategy
        ): LocalSourceReader
    }

    // TODO: в базовый класс...
    private val recursiveDirReader: RecursiveDirReader? by lazy {
        recursiveDirReaderFactory.create(StorageType.LOCAL, authToken)
    }

    // TODO: в базовый класс...
    @Throws(FileLister.NotADirException::class)
    override suspend fun read(sourcePath: String) {

        recursiveDirReader?.getRecursiveList(
            path = sourcePath,
            sortingMode = SimpleSortingMode.NAME,
            reverseOrder = false,
            foldersFirst = true,
            dirMode = false
        )?.forEach { fileListItem: RecursiveDirReader.FileListItem ->

            val relativeParentDirPath = calculateRelativeParentDirPath(fileListItem, sourcePath)

            val existingObject = syncObjectReader.getSyncObject(fileListItem.name, relativeParentDirPath)

            if (null == existingObject) {
                SyncObject.createAsNew(
                    taskId = taskId,
                    fsItem = fileListItem,
                    relativeParentDirPath = relativeParentDirPath,
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