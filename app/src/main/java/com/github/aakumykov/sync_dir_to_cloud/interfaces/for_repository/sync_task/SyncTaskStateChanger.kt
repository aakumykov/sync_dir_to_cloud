package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.exceptions.TaskExecutionException
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState

interface SyncTaskStateChanger {

    suspend fun changeSchedulingState(taskId: String, newState: ExecutionState, errorMsg: String = "")

    suspend fun changeExecutionState(taskId: String, newState: ExecutionState, errorMsg: String = "")

    suspend fun changeSyncTaskEnabled(taskId: String, isEnabled: Boolean)

    suspend fun setSourceReadingState(taskId: String, state: ExecutionState, errorMsg: String? = null)

    suspend fun resetSourceReadingBadState(taskId: String)

    suspend fun setRunningState(taskId: String)
    suspend fun setSuccessState(taskId: String)
    suspend fun setErrorState(taskId: String, exception: Exception)
}