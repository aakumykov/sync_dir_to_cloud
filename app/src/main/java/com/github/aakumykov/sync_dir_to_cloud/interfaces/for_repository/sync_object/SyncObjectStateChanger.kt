package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

interface SyncObjectStateChanger {
    suspend fun changeState(syncObjectId: String, state: SyncObject.State)
    suspend fun setErrorState(syncObjectId: String, errorMsg: String)
}