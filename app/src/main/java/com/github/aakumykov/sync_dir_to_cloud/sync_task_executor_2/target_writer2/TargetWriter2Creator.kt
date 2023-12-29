package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer2

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

class TargetWriter2Creator @Inject constructor(
    private val targetWritersMap: Map<StorageType,TargetWriter2.Factory>
) {
    fun create(storageType: StorageType, authToken: String): TargetWriter2? {
        return targetWritersMap[storageType]?.create(authToken)
    }
}