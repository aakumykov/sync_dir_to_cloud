package com.github.aakumykov.sync_dir_to_cloud.repository

import android.content.res.Resources
import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ExecutionLogDAO
import javax.inject.Inject

class ExecutionLogRepository @Inject constructor(
    private val executionLogDAO: ExecutionLogDAO,
)
    : ExecutionLogger, ExecutionLogReader
{
    override suspend fun log(executionLogItem: ExecutionLogItem) {
        executionLogDAO.addItem(executionLogItem)
    }

    override fun getExecutionLog(taskId: String, executionId: String): LiveData<List<ExecutionLogItem>> {
        return executionLogDAO.getLogsAsLiveData(taskId, executionId)
    }
}