package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer

import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_file_stream_supplier.SourceFileStreamSupplier

interface TargetWriter {
    suspend fun writeToTarget(sourceFileStreamSupplier: SourceFileStreamSupplier,
                              overwriteIfExists: Boolean = true)
}