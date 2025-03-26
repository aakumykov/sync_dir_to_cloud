package com.github.aakumykov.sync_dir_to_cloud.repository

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncOperationLoggerDAO
import javax.inject.Inject

class SyncOperationLoggerRepository @Inject constructor(
    private val syncOperationLoggerDAO: SyncOperationLoggerDAO
){
    suspend fun add(syncOperationLogItem: SyncOperationLogItem) {
        syncOperationLoggerDAO.add(syncOperationLogItem)
    }

    suspend fun list(taskId: String, executionId: String): List<SyncOperationLogItem> {
        return syncOperationLoggerDAO.list(taskId, executionId)
    }
}
