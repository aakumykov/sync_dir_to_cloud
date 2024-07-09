package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

// TODO: дать более осмысленное название / разделить на отдельные интерфейсы
interface SyncObjectStateChanger {

    suspend fun changeSyncState(objectId: String, syncState: ExecutionState, errorMsg: String = "")

    // TODO: переименовать в setSyncTime()
    // TODO: перенести в SyncObjectUpdater
    suspend fun setSyncDate(objectId: String, date: Long)

    suspend fun changeModificationState(
        syncObject: SyncObject,
        modificationState: ModificationState
    )

    suspend fun markAsSuccessfullySynced(objectId: String)
    suspend fun markAsBusy(objectId: String)
    suspend fun markAsError(objectId: String, errorMsg: String)

    suspend fun setSourceReadingState(objectId: String, state: ExecutionState, errorMsg: String? = null)
    suspend fun setTargetReadingState(objectId: String, state: ExecutionState, errorMsg: String? = null)
    suspend fun setBackupState(objectId: String, state: ExecutionState, errorMsg: String? = null)
    suspend fun setDeletionState(objectId: String, state: ExecutionState, errorMsg: String? = null)
    suspend fun setRestorationState(objectId: String, state: ExecutionState, errorMsg: String? = null)
    suspend fun setSyncState(objectId: String, state: ExecutionState, errorMsg: String? = null)
}