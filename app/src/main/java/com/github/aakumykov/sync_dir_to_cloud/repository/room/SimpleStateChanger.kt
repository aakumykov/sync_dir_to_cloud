package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Transaction
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

abstract class SimpleStateChanger {

    @Transaction @Update
    suspend fun setIdleState(taskId: String) {
        setStateNotUseDirectly(taskId, SyncTask.SimpleState.IDLE)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setBusyState(taskId: String) {
        setStateNotUseDirectly(taskId, SyncTask.SimpleState.BUSY)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setErrorState(taskId: String, errorMsg: String) {
        setStateNotUseDirectly(taskId, SyncTask.SimpleState.ERROR)
        setErrorMsgNotUseDirectly(taskId, errorMsg)
    }


    abstract suspend fun setStateNotUseDirectly(taskId: String, state: SyncTask.SimpleState)
    abstract suspend fun setErrorMsgNotUseDirectly(taskId: String, errorMsg: String)
}