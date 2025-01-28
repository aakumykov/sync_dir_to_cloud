package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

// TODO: дать более осмысленное название / разделить на отдельные интерфейсы
interface SyncObjectStateChanger {

    suspend fun changeSyncState(objectId: String, syncState: ExecutionState, errorMsg: String = "")

    // TODO: переименовать в setSyncTime()
    // TODO: перенести в SyncObjectUpdater
    suspend fun setSyncDate(objectId: String, date: Long)

    suspend fun changeModificationState(
        syncObject: SyncObject,
        stateInStorage: StateInStorage
    )

    suspend fun markAsSuccessfullySynced(objectId: String)
    suspend fun markAsBusy(objectId: String)
    suspend fun markAsError(objectId: String, errorMsg: String)
    suspend fun markAsError(objectId: String, t: Throwable)

    suspend fun setTargetReadingState(objectId: String, state: ExecutionState, errorMsg: String = "")
    suspend fun setBackupState(objectId: String, state: ExecutionState, errorMsg: String = "")
    suspend fun setDeletionState(objectId: String, state: ExecutionState, errorMsg: String = "")
    suspend fun setRestorationState(objectId: String, state: ExecutionState, errorMsg: String = "")
    suspend fun setSyncState(objectId: String, state: ExecutionState, errorMsg: String = "")
}