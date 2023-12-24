package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.creator

import com.github.aakumykov.sync_dir_to_cloud.di.factories.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.interfaces.SourceReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.interfaces.SourceReaderAssistedFactory
import javax.inject.Inject

class SourceReaderCreator @Inject constructor(
    private val factoriesMap: Map<StorageType, @JvmSuppressWildcards SourceReaderAssistedFactory>,
) {
    fun create(sourceType: StorageType?, taskId: String, sourceAuthToken: String?): SourceReader? {

        return sourceAuthToken?.let {
            factoriesMap[sourceType]?.create(sourceAuthToken, taskId)
        }
    }
}