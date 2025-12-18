package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Transaction
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import kotlinx.coroutines.CancellationException

abstract class SyncStateChanger {

    @Transaction @Update
    suspend fun setIdleState(taskId: String) {
//        MyLogger.d(TAG, "setIdleState() called with: taskId = $taskId")
        setStateNotUseDirectly(taskId, ExecutionState.NEVER)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setBusyState(taskId: String) {
        setStateNotUseDirectly(taskId, ExecutionState.RUNNING)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setSuccessState(taskId: String) {
        setStateNotUseDirectly(taskId, ExecutionState.SUCCESS)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setCancelledState(taskId: String, errorMsg: String) {
        setStateNotUseDirectly(taskId, ExecutionState.CANCELLED)
        setErrorMsgNotUseDirectly(taskId, errorMsg)
    }

    @Transaction @Update
    suspend fun setErrorState(taskId: String, errorMsg: String) {
        setStateNotUseDirectly(taskId, ExecutionState.ERROR)
        setErrorMsgNotUseDirectly(taskId, errorMsg)
    }


    abstract suspend fun setStateNotUseDirectly(taskId: String, state: ExecutionState)
    abstract suspend fun setErrorMsgNotUseDirectly(taskId: String, errorMsg: String)

    companion object {
        val TAG: String = SyncStateChanger::class.java.simpleName
    }
}