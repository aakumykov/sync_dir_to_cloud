package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log.SyncTaskLogDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log.TaskStateLogger
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskLogDAO
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Deprecated("Переименовать в TaskLogRepository")
class SyncTaskLogRepository @Inject constructor(
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
    private val syncTaskLogDAO: SyncTaskLogDAO
)
    : SyncTaskLogDeleter, TaskStateLogger
{
    fun getLogsForTask(taskId: String): LiveData<List<ExecutionLogItem>> {
        return syncTaskLogDAO.getLogsForTask(taskId)
    }


    override suspend fun deleteLogsForTask(taskId: String) {
        withContext(coroutineDispatcher) {
            syncTaskLogDAO.deleteEntriesForTask(taskId)
        }
    }


    override suspend fun logRunning(executionLogItem: ExecutionLogItem) {
        withContext(coroutineDispatcher) {
            syncTaskLogDAO.addTaskLog(executionLogItem)
        }
    }

    override suspend fun logSuccess(executionLogItem: ExecutionLogItem) {
        withContext(coroutineDispatcher) {
            syncTaskLogDAO.updateAsSuccess(
                executionLogItem.taskId,
                executionLogItem.executionId,
                currentTime(),
            )
        }
    }

    override suspend fun logError(executionLogItem: ExecutionLogItem) {
        withContext(coroutineDispatcher) {
            syncTaskLogDAO.updateAsError(
                executionLogItem.taskId,
                executionLogItem.executionId,
                finishTime = currentTime(),
                errorMsg = executionLogItem.errorMsg,
            )
        }
    }
}