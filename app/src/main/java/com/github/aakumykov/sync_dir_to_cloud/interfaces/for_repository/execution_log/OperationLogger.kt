package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem

interface OperationLogger {
    suspend fun log(taskExecutionLogItem: TaskExecutionLogItem)
    suspend fun updateLog(taskExecutionLogItem: TaskExecutionLogItem)
}