package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier

import java.io.InputStream

interface SourceFileStreamSupplier {
    suspend fun getSourceFileStream(absolutePath: String): Result<InputStream>
    fun getSourceFileStreamSimple(absolutePath: String): Result<InputStream>
}