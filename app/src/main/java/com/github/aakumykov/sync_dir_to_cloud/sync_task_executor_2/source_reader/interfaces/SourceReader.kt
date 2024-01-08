package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.source_reader.interfaces

interface SourceReader {
    suspend fun read(sourcePath: String)
}