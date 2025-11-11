package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

// TODO: дать более осмысленное название / разделить на отдельные интерфейсы
// FIXME: путаю с SyncObjectUpdater-ом
interface SyncObjectStateChanger {

    suspend fun changeSyncState(objectId: String, syncState: ExecutionState, errorMsg: String = "")

    // TODO: переименовать в setSyncTime()
    // TODO: перенести в SyncObjectUpdater
    @Deprecated("Не используется")
    suspend fun setSyncDate(objectId: String, date: Long)

    @Deprecated("Не используется")
    suspend fun changeModificationState(
        syncObject: SyncObject,
        stateInStorage: StateInStorage
    )

    suspend fun markAsSuccessfullySynced(objectId: String)
    @Deprecated("Не используется") suspend fun markAsBusy(objectId: String)
    @Deprecated("Не используется") suspend fun markAsError(objectId: String, errorMsg: String)
    @Deprecated("Не используется") suspend fun markAsError(objectId: String, t: Throwable)

    @Deprecated("Не используется") suspend fun setTargetReadingState(objectId: String, state: ExecutionState, errorMsg: String = "")
    @Deprecated("Не используется") suspend fun setBackupState(objectId: String, state: ExecutionState, errorMsg: String = "")
    @Deprecated("Не используется") suspend fun setDeletionState(objectId: String, state: ExecutionState, errorMsg: String = "")
    @Deprecated("Не используется") suspend fun setRestorationState(objectId: String, state: ExecutionState, errorMsg: String = "")
    @Deprecated("Не используется") suspend fun setSyncState(objectId: String, state: ExecutionState, errorMsg: String = "")
}