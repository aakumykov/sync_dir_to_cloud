package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_file_stream

import java.io.InputStream

interface SourceFileStreamSupplier {
    suspend fun getSourceFileStream(absolutePath: String): Result<InputStream>
}