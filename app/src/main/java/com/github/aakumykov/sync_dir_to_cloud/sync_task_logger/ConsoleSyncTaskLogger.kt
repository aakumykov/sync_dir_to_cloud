package com.github.aakumykov.sync_dir_to_cloud.sync_task_logger

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import javax.inject.Inject

@ExecutionScope
class ConsoleSyncTaskLogger @Inject constructor(
    private val logTag: String
): SyncTaskLogger {
    override suspend fun log(executionLogItem: ExecutionLogItem) {

    }
}