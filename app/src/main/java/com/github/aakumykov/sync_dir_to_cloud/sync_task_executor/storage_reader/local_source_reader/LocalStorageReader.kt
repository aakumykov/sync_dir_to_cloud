package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.local_source_reader

import com.github.aakumykov.sync_dir_to_cloud.AssistedArgName
import com.github.aakumykov.sync_dir_to_cloud.FileListerFactoryCreator
import com.github.aakumykov.sync_dir_to_cloud.di.factories.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.BasicStorageReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy.ChangesDetectionStrategy
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class LocalStorageReader @AssistedInject constructor(
    @Assisted(AssistedArgName.AUTH_TOKEN) authToken: String,
    @Assisted(AssistedArgName.TASK_ID) taskId: String,
    @Assisted changesDetectionStrategy: ChangesDetectionStrategy,
    fileListerFactoryCreator: FileListerFactoryCreator,
    recursiveDirReaderFactory: RecursiveDirReaderFactory,
    syncObjectReader: SyncObjectReader,
    syncObjectAdder: SyncObjectAdder,
    syncObjectUpdater: SyncObjectUpdater,
    syncObjectStateChanger: SyncObjectStateChanger,
)
    // TODO: делегировать ему, чтобы не вызывать этот громоздкий конструктор.
    : BasicStorageReader(
        taskId = taskId,
        recursiveDirReaderFactory = recursiveDirReaderFactory,
        fileListerFactoryCreator = fileListerFactoryCreator,
        changesDetectionStrategy = changesDetectionStrategy,
        authToken = authToken,
        syncObjectReader = syncObjectReader,
        syncObjectUpdater = syncObjectUpdater,
        syncObjectAdder = syncObjectAdder,
        syncObjectStateChanger = syncObjectStateChanger,
    )
{
    override val storageType: StorageType
        get() = StorageType.LOCAL
}