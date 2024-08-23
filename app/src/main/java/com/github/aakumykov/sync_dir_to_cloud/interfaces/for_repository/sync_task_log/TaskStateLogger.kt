package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry

interface TaskStateLogger {
    suspend fun logRunning(taskLogEntry: TaskLogEntry)
    suspend fun logSuccess(taskLogEntry: TaskLogEntry)
    suspend fun logError(taskLogEntry: TaskLogEntry)
}