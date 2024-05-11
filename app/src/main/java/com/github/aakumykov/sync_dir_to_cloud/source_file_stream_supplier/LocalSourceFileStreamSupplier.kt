package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier

import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class LocalSourceFileStreamSupplier @Inject constructor() : SourceFileStreamSupplier {

    override suspend fun getSourceFileStream(absolutePath: String): Result<FileInputStream> {
        return try {
            Result.success(File(absolutePath).inputStream())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
