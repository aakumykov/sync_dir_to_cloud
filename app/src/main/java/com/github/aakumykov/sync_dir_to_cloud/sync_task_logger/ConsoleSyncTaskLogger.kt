package com.github.aakumykov.sync_dir_to_cloud.sync_task_logger

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry

class ConsoleSyncTaskLogger(private val logTag: String): SyncTaskLogger {

    override suspend fun log(taskLogEntry: TaskLogEntry) {

    }
}