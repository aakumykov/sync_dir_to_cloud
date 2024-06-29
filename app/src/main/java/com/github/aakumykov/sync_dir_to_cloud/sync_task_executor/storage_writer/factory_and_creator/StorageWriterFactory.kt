package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer.factory_and_creator

import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer.StorageWriter

interface StorageWriterFactory {
    fun create(authToken: String,
               taskId: String,
               sourceDirPath: String,
               targetDirPath: String,
    ): StorageWriter
}