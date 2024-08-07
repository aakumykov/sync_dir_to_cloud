package com.github.aakumykov.sync_dir_to_cloud.sync_task_logger

interface SyncTaskLogger {
    suspend fun log(taskLog: TaskLog)
}