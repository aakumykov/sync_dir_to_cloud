package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncState

interface SyncObjectStateChanger {
    suspend fun changeExecutionState(syncObjectId: String, syncState: SyncState, errorMsg: String = "")
    // TODO: переименовать в setSyncTime()
    suspend fun setSyncDate(id: String, date: Long)
}