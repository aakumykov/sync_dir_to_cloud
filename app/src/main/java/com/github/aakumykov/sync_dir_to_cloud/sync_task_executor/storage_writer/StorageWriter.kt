package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer

import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier

interface StorageWriter {
    suspend fun writeToTarget(sourceFileStreamSupplier: SourceFileStreamSupplier,
                              overwriteIfExists: Boolean = true)
}