package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.target_writer

interface TargetWriter {
    suspend fun writeToTarget(overwriteIfExists: Boolean = true)
}