package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader

import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.AssistedArgName
import com.github.aakumykov.sync_dir_to_cloud.BuildConfig
import com.github.aakumykov.sync_dir_to_cloud.di.factories.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.interfaces.SourceReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.interfaces.SourceReaderAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.utils.calculateRelativeParentDirPath
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

class LocalSourceReader @AssistedInject constructor(
    @Assisted(AssistedArgName.AUTH_TOKEN) private val authToken: String,
    @Assisted(AssistedArgName.TASK_ID) private val taskId: String,
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,
    private val syncObjectAdder: SyncObjectAdder,
): SourceReader {

    private var stopFlag: Boolean = false

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

        stopFlag = false

        run loop@ {

            recursiveDirReader?.getRecursiveList(sourcePath)?.forEach { fileListItem: RecursiveDirReader.FileListItem ->

                if (stopFlag) return@loop

                val relativeParentDirPath = calculateRelativeParentDirPath(fileListItem, sourcePath)

                val syncObject = SyncObject.create(
                    taskId = taskId,
                    fsItem = fileListItem,
                    relativeParentDirPath = relativeParentDirPath
                )

                syncObjectAdder.addSyncObject(syncObject)

                if (BuildConfig.DEBUG)
                    delay(5000L)
            }
        }
    }

    // TODO: испытать, добавив искусственные задержки при чтении
    override fun stopReading() {
        stopFlag = true
    }
}