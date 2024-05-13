package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.creator

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.interfaces.SourceReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.interfaces.SourceReaderAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.strategy.ChangesDetectionStrategy
import javax.inject.Inject

@Deprecated("Переименовать в StorageReaderCreator")
class SourceReaderCreator @Inject constructor(
    private val factoriesMap: Map<StorageType, @JvmSuppressWildcards SourceReaderAssistedFactory>,
) {
    fun create(sourceType: StorageType?,
               sourceAuthToken: String?,
               taskId: String,
               changesDetectionStrategy: ChangesDetectionStrategy
    ): SourceReader? {
        return sourceAuthToken?.let {
            factoriesMap[sourceType]?.create(sourceAuthToken, taskId, changesDetectionStrategy)
        }
    }
}