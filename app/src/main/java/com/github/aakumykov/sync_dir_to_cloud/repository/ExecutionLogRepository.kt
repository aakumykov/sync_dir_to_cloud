package com.github.aakumykov.sync_dir_to_cloud.repository

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ExecutionLogDAO
import javax.inject.Inject

class ExecutionLogRepository @Inject constructor(
    private val executionLogDAO: ExecutionLogDAO,
) {
    suspend fun addItem(executionLogItem: ExecutionLogItem) {
        executionLogDAO.addItem(executionLogItem)
    }
}