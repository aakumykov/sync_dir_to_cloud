package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer.factory_and_creator

import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer.TargetWriter

interface TargetWriterFactory {
    fun create(authToken: String,
               taskId: String,
               sourceDirPath: String,
               targetDirPath: String,
    ): TargetWriter
}