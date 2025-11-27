package com.github.aakumykov.sync_dir_to_cloud.repository.sync_operation_log_repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncOperationLoggerDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncOperationLogRepository @Inject constructor(
    private val syncOperationLoggerDAO: SyncOperationLoggerDAO,
)
    : SyncOperationLogReader
{
    suspend fun add(syncOperationLogItem: SyncOperationLogItem) {
        Log.d(TAG, "add(): syncOperationLogItem = $syncOperationLogItem")
        syncOperationLoggerDAO.add(syncOperationLogItem)
    }

    override suspend fun get(operationId: String): SyncOperationLogItem? {
        return syncOperationLoggerDAO.get(operationId)
    }

    override fun listAsLiveData(taskId: String, executionId: String): LiveData<List<SyncOperationLogItem>> {
        return syncOperationLoggerDAO.listAsLiveData(taskId, executionId)
    }

    suspend fun updateLogItemState(logItemId: String, operationState: OperationState) {
        Log.d(
            TAG,
            "updateLogItemState(): logItemId = $logItemId, operationState = $operationState"
        )
        withContext(Dispatchers.IO) {
            syncOperationLoggerDAO.updateState(logItemId, operationState)
        }
    }

    suspend fun updateLogItemState(logItemId: String, operationState: OperationState, errorMsg: String) {
        withContext(Dispatchers.IO) {
            Log.d(
                TAG,
                "updateLogItemState(): logItemId = $logItemId, operationState = $operationState, errorMsg = $errorMsg"
            )
            syncOperationLoggerDAO.updateStateAndError(logItemId, operationState, errorMsg)
        }
    }

    companion object {
        val TAG: String = SyncOperationLogRepository::class.java.simpleName
    }
}
