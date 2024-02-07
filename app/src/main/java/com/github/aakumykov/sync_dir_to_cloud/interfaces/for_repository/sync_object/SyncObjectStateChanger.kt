package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState

interface SyncObjectStateChanger {
    suspend fun changeExecutionState(syncObjectId: String, executionState: ExecutionState, errorMsg: String = "")
    suspend fun setSyncDate(id: String, date: Long)
}