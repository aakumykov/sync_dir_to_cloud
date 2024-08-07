package com.github.aakumykov.sync_dir_to_cloud.repository

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.sync_task_logger.TaskLog
import com.github.aakumykov.sync_dir_to_cloud.sync_task_logger.TaskLogDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskLogRepository @Inject constructor(
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
    private val taskLogDAO: TaskLogDAO
) {
    suspend fun addLogEntry(taskLog: TaskLog) {
        withContext(coroutineDispatcher) {
            taskLogDAO.addTaskLog(taskLog)
        }
    }

    suspend fun removeLogEntriesForTask(taskId: String){
        withContext(coroutineDispatcher) {
            taskLogDAO.deleteEntriesForTask(taskId)
        }
    }
}