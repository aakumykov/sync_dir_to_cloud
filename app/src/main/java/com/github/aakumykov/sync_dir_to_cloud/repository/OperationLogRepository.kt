package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
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
    override suspend fun log(executionLogItem: ExecutionLogItem) {
        executionLogDAO.addItem(executionLogItem)
    }

    override suspend fun updateLog(executionLogItem: ExecutionLogItem) {
        executionLogDAO.updateItem(executionLogItem)
    }

    override fun getExecutionLog(taskId: String, executionId: String): LiveData<List<ExecutionLogItem>> {
        return executionLogDAO.getLogsAsLiveData(taskId, executionId)
    }

    @Deprecated("удалить")
    override suspend fun clearExecutionLog() {
        executionLogDAO.clear()
    }
}