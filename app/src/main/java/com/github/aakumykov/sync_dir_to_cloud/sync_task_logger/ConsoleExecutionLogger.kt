package com.github.aakumykov.sync_dir_to_cloud.sync_task_logger

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import javax.inject.Inject

@ExecutionScope
class ConsoleExecutionLogger @Inject constructor(
    private val logTag: String
): ExecutionLogger {
    override suspend fun log(executionLogItem: ExecutionLogItem) {

    }
}