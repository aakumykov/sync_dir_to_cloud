package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Transaction
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncState
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger

abstract class SyncStateChanger {

    @Transaction @Update
    suspend fun setIdleState(taskId: String) {
        MyLogger.d(TAG, "setIdleState() called with: taskId = $taskId")
        setStateNotUseDirectly(taskId, SyncState.NEVER)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setBusyState(taskId: String) {
        MyLogger.d(TAG, "setBusyState() called with: taskId = $taskId")
        setStateNotUseDirectly(taskId, SyncState.RUNNING)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setSuccessState(taskId: String) {
        MyLogger.d(TAG, "setSuccessState() called with: taskId = $taskId")
        setStateNotUseDirectly(taskId, SyncState.SUCCESS)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setErrorState(taskId: String, errorMsg: String) {
        MyLogger.d(TAG, "setErrorState() called with: taskId = $taskId, errorMsg = $errorMsg")
        setStateNotUseDirectly(taskId, SyncState.ERROR)
        setErrorMsgNotUseDirectly(taskId, errorMsg)
    }


    abstract suspend fun setStateNotUseDirectly(taskId: String, state: SyncState)
    abstract suspend fun setErrorMsgNotUseDirectly(taskId: String, errorMsg: String)

    companion object {
        val TAG: String = SyncStateChanger::class.java.simpleName
    }
}