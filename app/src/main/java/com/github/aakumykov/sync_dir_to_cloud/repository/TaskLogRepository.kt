package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskLogDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskLogRepository @Inject constructor(
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
    private val syncTaskLogDAO: SyncTaskLogDAO
) {
    suspend fun addLogEntry(taskLogEntry: TaskLogEntry) {
        withContext(coroutineDispatcher) {
            syncTaskLogDAO.addTaskLog(taskLogEntry)
        }
    }

    suspend fun removeLogEntriesForTask(taskId: String){
        withContext(coroutineDispatcher) {
            syncTaskLogDAO.deleteEntriesForTask(taskId)
        }
    }

    fun getLogsForTask(taskId: String): LiveData<List<TaskLogEntry>> {
        return syncTaskLogDAO.getLogsForTask(taskId)
    }
}