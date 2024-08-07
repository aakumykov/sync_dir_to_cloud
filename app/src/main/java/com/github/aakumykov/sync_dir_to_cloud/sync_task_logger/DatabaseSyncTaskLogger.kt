package com.github.aakumykov.sync_dir_to_cloud.sync_task_logger

import com.github.aakumykov.sync_dir_to_cloud.repository.TaskLogRepository

class DatabaseSyncTaskLogger (
    private val taskLogRepository: TaskLogRepository
): SyncTaskLogger {

    override suspend fun log(taskLog: TaskLog) {
        taskLogRepository.addLogEntry(taskLog)
    }
}