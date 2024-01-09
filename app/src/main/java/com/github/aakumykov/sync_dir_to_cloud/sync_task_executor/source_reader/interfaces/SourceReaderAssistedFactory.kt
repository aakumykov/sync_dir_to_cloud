package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.interfaces

interface SourceReaderAssistedFactory {
    fun create(authToken: String,
               taskId: String): SourceReader
}