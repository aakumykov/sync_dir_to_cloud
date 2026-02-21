package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState

// TODO: дать более осмысленное название / разделить на отдельные интерфейсы
// FIXME: путаю с SyncObjectUpdater-ом
interface SyncObjectStateChanger {
    suspend fun changeSyncState(objectId: String, syncState: ExecutionState, errorMsg: String = "")
    suspend fun markAsSuccessfullySynced(objectId: String)
}