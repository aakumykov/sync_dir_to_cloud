package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncState

interface SyncObjectStateChanger {
    suspend fun changeExecutionState(objectId: String, syncState: SyncState, errorMsg: String = "")
    // TODO: переименовать в setSyncTime()
    suspend fun setSyncDate(objectId: String, date: Long)
    suspend fun changeModificationState(objectId: String, modificationState: ModificationState)
}