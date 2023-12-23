package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.creator

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.interfaces.TargetWriter
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.interfaces.TargetWriterAssistedFactory
import javax.inject.Inject

class TargetWriterCreator @Inject constructor(
    private val map: Map<StorageType, @JvmSuppressWildcards TargetWriterAssistedFactory>
) {
    fun create(storageType: StorageType?, authToken: String?): TargetWriter? {
        return authToken?.let {
            map[storageType]?.create(authToken)
        }
    }
}