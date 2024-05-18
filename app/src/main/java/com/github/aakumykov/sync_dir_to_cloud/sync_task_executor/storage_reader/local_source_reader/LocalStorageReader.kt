package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.local_source_reader

import com.github.aakumykov.sync_dir_to_cloud.AssistedArgName
import com.github.aakumykov.sync_dir_to_cloud.di.factories.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectRepository
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.BasicStorageReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy.ChangesDetectionStrategy
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class LocalStorageReader @AssistedInject constructor(
    @Assisted(AssistedArgName.AUTH_TOKEN) authToken: String,
    @Assisted(AssistedArgName.TASK_ID) taskId: String,
    @Assisted changesDetectionStrategy: ChangesDetectionStrategy,
    recursiveDirReaderFactory: RecursiveDirReaderFactory,
    syncObjectRepository: SyncObjectRepository,
)
    // TODO: делегировать ему, чтобы не вызывать этот громоздкий конструктор.
    : BasicStorageReader(
        taskId = taskId,
        authToken = authToken,
        recursiveDirReaderFactory = recursiveDirReaderFactory,
        changesDetectionStrategy = changesDetectionStrategy,
        syncObjectRepository = syncObjectRepository,
    )
{
    override val storageType: StorageType
        get() = StorageType.LOCAL
}