package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem

interface ExecutionLogger {
    suspend fun addLogItem(executionLogItem: ExecutionLogItem)
}