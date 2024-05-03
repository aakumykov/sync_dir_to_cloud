package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_file_stream.SourceFileStreamSupplier
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class LocalSFSS : SourceFileStreamSupplier {
    override suspend fun getSourceFileStream(absolutePath: String): Result<FileInputStream> {
        return try {
            val file = File(absolutePath)
//            val is1 = FileInputStream(file) // Странное предупреждение в сравнении с file.inputStream()
            val is2 = file.inputStream()
            Result.success(is2)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
