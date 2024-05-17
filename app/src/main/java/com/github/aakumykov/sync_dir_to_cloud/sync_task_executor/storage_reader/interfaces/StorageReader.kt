package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.interfaces

import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectRepository

interface StorageReader {
    suspend fun read(sourcePath: String?, syncObjectRepository: SyncObjectRepository)
}