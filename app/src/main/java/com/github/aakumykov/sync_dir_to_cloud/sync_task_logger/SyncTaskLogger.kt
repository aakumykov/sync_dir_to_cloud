package com.github.aakumykov.sync_dir_to_cloud.sync_task_logger

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry

@Deprecated("TODO: Сделать синглтоном")
interface SyncTaskLogger {
    suspend fun log(taskLogEntry: TaskLogEntry)
}