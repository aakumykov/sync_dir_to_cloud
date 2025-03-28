package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
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

    fun listAsLiveData(taskId: String, executionId: String): LiveData<List<SyncOperationLogItem>> {
        return syncOperationLoggerDAO.listAsLiveData(taskId, executionId)
    }

    suspend fun updateLogItemState(logItemId: String, operationState: OperationState) {
        return syncOperationLoggerDAO.updateState(logItemId, operationState)
    }

    suspend fun updateLogItemState(logItemId: String, operationState: OperationState, errorMsg: String) {
        return syncOperationLoggerDAO.updateStateAndError(logItemId, operationState, errorMsg)
    }
}
