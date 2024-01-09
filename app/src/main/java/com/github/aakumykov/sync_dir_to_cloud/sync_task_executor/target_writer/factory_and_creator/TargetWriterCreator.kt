package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer.factory_and_creator

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer.TargetWriter
import javax.inject.Inject

class TargetWriterCreator @Inject constructor(
    private val map: Map<StorageType, @JvmSuppressWildcards TargetWriterFactory>
) {
    fun create(storageType: StorageType,
               authToken: String,
               taskId: String,
               sourceDirPath: String,
               targetDirPath: String
    ): TargetWriter?
    {
        return map[storageType]?.create(
            authToken,
            taskId,
            sourceDirPath,
            targetDirPath)
    }
}