package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log.SyncTaskLogDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log.TaskStateLogger
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskLogDAO
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskLogRepository @Inject constructor(
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
    private val syncTaskLogDAO: SyncTaskLogDAO
)
    : SyncTaskLogDeleter, TaskStateLogger
{
    fun getLogsForTask(taskId: String): LiveData<List<TaskLogEntry>> {
        return syncTaskLogDAO.getLogsForTask(taskId)
    }


    override suspend fun deleteLogsForTask(taskId: String) {
        withContext(coroutineDispatcher) {
            syncTaskLogDAO.deleteEntriesForTask(taskId)
        }
    }


    override suspend fun logRunning(taskLogEntry: TaskLogEntry) {
        withContext(coroutineDispatcher) {
            syncTaskLogDAO.addTaskLog(taskLogEntry)
        }
    }

    override suspend fun logSuccess(taskLogEntry: TaskLogEntry) {
        withContext(coroutineDispatcher) {
            syncTaskLogDAO.updateAsSuccess(
                taskLogEntry.taskId,
                taskLogEntry.executionId,
                currentTime,
            )
        }
    }

    override suspend fun logError(taskLogEntry: TaskLogEntry) {
        withContext(coroutineDispatcher) {
            syncTaskLogDAO.updateAsError(
                taskLogEntry.taskId,
                taskLogEntry.executionId,
                finishTime = currentTime,
                errorMsg = taskLogEntry.errorMsg,
            )
        }
    }
}