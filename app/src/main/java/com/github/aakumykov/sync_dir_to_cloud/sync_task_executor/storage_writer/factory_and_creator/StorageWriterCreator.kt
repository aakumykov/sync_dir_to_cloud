package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer.factory_and_creator

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer.StorageWriter
import javax.inject.Inject

class StorageWriterCreator @Inject constructor(
    private val map: Map<StorageType, @JvmSuppressWildcards StorageWriterFactory>
) {
    fun create(storageType: StorageType,
               authToken: String,
               taskId: String,
               sourceDirPath: String,
               targetDirPath: String
    ): StorageWriter?
    {
        return map[storageType]?.create(
            authToken,
            taskId,
            sourceDirPath,
            targetDirPath)
    }
}