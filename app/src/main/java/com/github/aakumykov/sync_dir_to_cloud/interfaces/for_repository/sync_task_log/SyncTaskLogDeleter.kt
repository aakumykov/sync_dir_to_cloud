package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log

@Deprecated("Переименовать в TaskLogDeleter")
interface SyncTaskLogDeleter {
    suspend fun deleteLogsForTask(taskId: String)
}