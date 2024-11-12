package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem

interface TaskStateLogger {
    suspend fun logRunning(executionLogItem: ExecutionLogItem)
    suspend fun logSuccess(executionLogItem: ExecutionLogItem)
    suspend fun logError(executionLogItem: ExecutionLogItem)
}