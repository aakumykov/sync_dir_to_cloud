package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_file_stream.SourceFileStreamSupplier
import java.io.File
import java.io.FileInputStream

class LocalSourceFileStreamSupplier : SourceFileStreamSupplier {
    override suspend fun getSourceFileStream(absolutePath: String): Result<FileInputStream> {
        return try {
            Result.success(File(absolutePath).inputStream())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
