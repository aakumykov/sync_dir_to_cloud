package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Transaction
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger

abstract class ExecutionStateChanger {

    @Transaction @Update
    suspend fun setIdleState(taskId: String) {
        MyLogger.d(TAG, "setIdleState() called with: taskId = $taskId")
        setStateNotUseDirectly(taskId, ExecutionState.NEVER)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setBusyState(taskId: String) {
        MyLogger.d(TAG, "setBusyState() called with: taskId = $taskId")
        setStateNotUseDirectly(taskId, ExecutionState.RUNNING)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setSuccessState(taskId: String) {
        MyLogger.d(TAG, "setSuccessState() called with: taskId = $taskId")
        setStateNotUseDirectly(taskId, ExecutionState.SUCCESS)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setErrorState(taskId: String, errorMsg: String) {
        MyLogger.d(TAG, "setErrorState() called with: taskId = $taskId, errorMsg = $errorMsg")
        setStateNotUseDirectly(taskId, ExecutionState.ERROR)
        setErrorMsgNotUseDirectly(taskId, errorMsg)
    }


    abstract suspend fun setStateNotUseDirectly(taskId: String, state: ExecutionState)
    abstract suspend fun setErrorMsgNotUseDirectly(taskId: String, errorMsg: String)

    companion object {
        val TAG: String = ExecutionStateChanger::class.java.simpleName
    }
}