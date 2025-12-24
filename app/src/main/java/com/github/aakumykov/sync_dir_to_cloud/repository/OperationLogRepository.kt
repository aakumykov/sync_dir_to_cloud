package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogCleaner
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.OperationLogger
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ExecutionLogDAO
import javax.inject.Inject

class OperationLogRepository @Inject constructor(
    private val executionLogDAO: ExecutionLogDAO,
)
    : OperationLogger, ExecutionLogReader, ExecutionLogCleaner
{
    override suspend fun log(taskExecutionLogItem: TaskExecutionLogItem) {
        executionLogDAO.addItem(taskExecutionLogItem)
    }

    override suspend fun updateLog(taskExecutionLogItem: TaskExecutionLogItem) {
        executionLogDAO.updateItem(taskExecutionLogItem)
    }

    override fun getExecutionLog(taskId: String, executionId: String): LiveData<List<TaskExecutionLogItem>> {
        return executionLogDAO.getLogsAsLiveData(taskId, executionId)
    }

    @Deprecated("удалить")
    override suspend fun clearExecutionLog() {
        executionLogDAO.clear()
    }
}