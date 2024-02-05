package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SimpleState

interface SyncObjectStateChanger {

    suspend fun changeExecutionState(syncObjectId: String, state: SimpleState, errorMsg: String = "")

    @Deprecated("Использовать changeExecutionState()")
    suspend fun setErrorState(syncObjectId: String, errorMsg: String)
}