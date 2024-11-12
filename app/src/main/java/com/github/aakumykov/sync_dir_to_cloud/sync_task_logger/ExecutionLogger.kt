package com.github.aakumykov.sync_dir_to_cloud.sync_task_logger

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem

interface ExecutionLogger {
    suspend fun log(executionLogItem: ExecutionLogItem)
}