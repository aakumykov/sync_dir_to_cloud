package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log.SyncTaskLogDeleter
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskLogDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncTaskLogRepository @Inject constructor(
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
    private val syncTaskLogDAO: SyncTaskLogDAO
)
    : SyncTaskLogDeleter
{
    suspend fun addLogEntry(taskLogEntry: TaskLogEntry) {
        withContext(coroutineDispatcher) {
            syncTaskLogDAO.addTaskLog(taskLogEntry)
        }
    }

    fun getLogsForTask(taskId: String): LiveData<List<TaskLogEntry>> {
        return syncTaskLogDAO.getLogsForTask(taskId)
    }

    override suspend fun deleteLogsForTask(taskId: String) {
        withContext(coroutineDispatcher) {
            syncTaskLogDAO.deleteEntriesForTask(taskId)
        }
    }
}