package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Transaction
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger

abstract class SimpleStateChanger {

    @Transaction @Update
    suspend fun setIdleState(taskId: String) {
        MyLogger.d(TAG, "setIdleState() called with: taskId = $taskId")
        setStateNotUseDirectly(taskId, SyncTask.SimpleState.IDLE)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setBusyState(taskId: String) {
        MyLogger.d(TAG, "setBusyState() called with: taskId = $taskId")
        setStateNotUseDirectly(taskId, SyncTask.SimpleState.BUSY)
        setErrorMsgNotUseDirectly(taskId, "")
    }

    @Transaction @Update
    suspend fun setErrorState(taskId: String, errorMsg: String) {
        MyLogger.d(TAG, "setErrorState() called with: taskId = $taskId, errorMsg = $errorMsg")
        setStateNotUseDirectly(taskId, SyncTask.SimpleState.ERROR)
        setErrorMsgNotUseDirectly(taskId, errorMsg)
    }


    abstract suspend fun setStateNotUseDirectly(taskId: String, state: SyncTask.SimpleState)
    abstract suspend fun setErrorMsgNotUseDirectly(taskId: String, errorMsg: String)

    companion object {
        val TAG: String = SimpleStateChanger::class.java.simpleName
    }
}