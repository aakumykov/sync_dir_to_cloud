package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

class TargetWriterCreator3 @Inject constructor(
    private val map: Map<StorageType, @JvmSuppressWildcards TargetWriterFactory3>
) {
    fun create(storageType: StorageType, authToken: String, taskId: String): TargetWriter3? {
        return map[storageType]?.create(authToken, taskId)
    }
}