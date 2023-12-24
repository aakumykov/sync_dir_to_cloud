package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader

import com.github.aakumykov.file_lister.FileLister
import com.github.aakumykov.sync_dir_to_cloud.ArgName
import com.github.aakumykov.sync_dir_to_cloud.di.factories.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.interfaces.SourceReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.interfaces.SourceReaderAssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.UUID
import javax.inject.Named

class LocalSourceReader @AssistedInject constructor(
    @Assisted(ArgName.AUTH_TOKEN) private val authToken: String,
    @Assisted(ArgName.TASK_ID) private val taskId: String,
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,
    private val syncObjectAdder: SyncObjectAdder,
): SourceReader {

    @AssistedFactory
    interface Factory : SourceReaderAssistedFactory {
        override fun create(@Assisted(ArgName.AUTH_TOKEN) authToken: String,
                            @Assisted(ArgName.TASK_ID) taskId: String
        ): LocalSourceReader
    }

    // TODO: в базовый класс...
    private val recursiveDirReader by lazy {
        recursiveDirReaderFactory.create(StorageType.LOCAL, authToken)
    }

    // TODO: в базовый класс...
    @Throws(FileLister.NotADirException::class)
    override suspend fun read(path: String) {

        recursiveDirReader?.getRecursiveList(path)?.forEach { fileListItem ->
            syncObjectAdder.addSyncObject(SyncObject.create(taskId, fileListItem))
        }
    }
}