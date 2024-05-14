package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.creator

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.interfaces.StorageReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.interfaces.StorageReaderAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy.ChangesDetectionStrategy
import javax.inject.Inject

class StorageReaderCreator @Inject constructor(
    private val factoriesMap: Map<StorageType, @JvmSuppressWildcards StorageReaderAssistedFactory>,
) {
    fun create(sourceType: StorageType?,
               sourceAuthToken: String?,
               taskId: String,
               changesDetectionStrategy: ChangesDetectionStrategy
    ): StorageReader? {
        return sourceAuthToken?.let {
            factoriesMap[sourceType]?.create(sourceAuthToken, taskId, changesDetectionStrategy)
        }
    }
}