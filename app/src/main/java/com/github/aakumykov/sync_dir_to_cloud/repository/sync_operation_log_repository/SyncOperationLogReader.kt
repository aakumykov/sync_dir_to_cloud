package com.github.aakumykov.sync_dir_to_cloud.repository.sync_operation_log_repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncOperationLogItem

interface SyncOperationLogReader {
    suspend fun list(taskId: String, executionId: String): List<SyncOperationLogItem>
    fun listAsLiveData(taskId: String, executionId: String): LiveData<List<SyncOperationLogItem>>
}
