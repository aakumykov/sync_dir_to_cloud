package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskStateChanger {

    @Deprecated("Скоро будет заменено на набор специфичных методов")
    suspend fun changeState(taskId: String, newSate: SyncTask.State)

    suspend fun changeSchedulingState(taskId: String, newState: ExecutionState, errorMsg: String = "")

    suspend fun changeExecutionState(taskId: String, newState: ExecutionState, errorMsg: String = "")

    suspend fun changeSyncTaskEnabled(taskId: String, isEnabled: Boolean)


    suspend fun setSourceReadingState(taskId: String, state: ExecutionState, errorMsg: String? = null)
    suspend fun setTargetReadingState(taskId: String, state: ExecutionState, errorMsg: String? = null)
}