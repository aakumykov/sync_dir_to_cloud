package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.yandex_source_reader

import com.github.aakumykov.sync_dir_to_cloud.AssistedArgName
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.interfaces.StorageReaderAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy.ChangesDetectionStrategy
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface YandexStorageReaderAssistedFactory : StorageReaderAssistedFactory {
    override fun create(@Assisted(AssistedArgName.AUTH_TOKEN) authToken: String,
                        @Assisted(AssistedArgName.TASK_ID) taskId: String,
                        changesDetectionStrategy: ChangesDetectionStrategy
    ): YandexStorageToDbReader
}