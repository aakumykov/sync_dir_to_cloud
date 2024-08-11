package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log

interface SyncTaskLogDeleter {
    suspend fun deleteLogsForTask(taskId: String)
}