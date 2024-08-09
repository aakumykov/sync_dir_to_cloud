package com.github.aakumykov.sync_dir_to_cloud.sync_task_logger

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.repository.TaskLogRepository

@ExecutionScope
class DatabaseSyncTaskLogger (
    private val taskLogRepository: TaskLogRepository
): SyncTaskLogger {

    override suspend fun log(taskLogEntry: TaskLogEntry) {
        Log.d(TAG, "log() called with: taskLogEntry = $taskLogEntry")
        taskLogRepository.addLogEntry(taskLogEntry)
    }

    companion object {
        val TAG: String = DatabaseSyncTaskLogger::class.java.simpleName
    }
}