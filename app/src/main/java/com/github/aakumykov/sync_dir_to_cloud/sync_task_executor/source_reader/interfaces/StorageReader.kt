package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.interfaces

interface StorageReader {
    suspend fun read(sourcePath: String)
}