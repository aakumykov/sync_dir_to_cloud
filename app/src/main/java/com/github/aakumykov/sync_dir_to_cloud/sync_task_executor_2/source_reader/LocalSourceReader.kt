package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader

import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.fs_item.utils.calculateRelativeDirPath
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.AssistedArgName
import com.github.aakumykov.sync_dir_to_cloud.di.factories.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.interfaces.SourceReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.interfaces.SourceReaderAssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class LocalSourceReader @AssistedInject constructor(
    @Assisted(AssistedArgName.AUTH_TOKEN) private val authToken: String,
    @Assisted(AssistedArgName.TASK_ID) private val taskId: String,
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,
    private val syncObjectAdder: SyncObjectAdder,
): SourceReader {

    @AssistedFactory
    interface Factory : SourceReaderAssistedFactory {
        override fun create(@Assisted(AssistedArgName.AUTH_TOKEN) authToken: String,
                            @Assisted(AssistedArgName.TASK_ID) taskId: String
        ): LocalSourceReader
    }

    // TODO: в базовый класс...
    private val recursiveDirReader: RecursiveDirReader? by lazy {
        recursiveDirReaderFactory.create(StorageType.LOCAL, authToken)
    }

    // TODO: в базовый класс...
    @Throws(FileLister.NotADirException::class)
    override suspend fun read(sourcePath: String) {

        recursiveDirReader?.getRecursiveList(sourcePath)?.forEach { fileListItem: RecursiveDirReader.FileListItem ->

            val relativeParentDirPath = calculateRelativeDirPath(fileListItem, sourcePath)

            val syncObject = SyncObject.create(
                taskId = taskId,
                fsItem = fileListItem,
                relativeParentDirPath = relativeParentDirPath
            )

            syncObjectAdder.addSyncObject(syncObject)
        }
    }
}