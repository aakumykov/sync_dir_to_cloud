package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.sync_task_logger.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.TaskLogDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskLogRepository @Inject constructor(
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
    private val taskLogDAO: TaskLogDAO
) {
    suspend fun addLogEntry(taskLogEntry: TaskLogEntry) {
        withContext(coroutineDispatcher) {
            taskLogDAO.addTaskLog(taskLogEntry)
        }
    }

    suspend fun removeLogEntriesForTask(taskId: String){
        withContext(coroutineDispatcher) {
            taskLogDAO.deleteEntriesForTask(taskId)
        }
    }

    fun getLogsForTask(taskId: String): LiveData<List<TaskLogEntry>> {
        return taskLogDAO.getLogsForTask(taskId)
    }
}