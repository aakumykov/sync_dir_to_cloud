package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SimpleState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskStateChanger {

    @Deprecated("Скоро будет заменено на набор специфичных методов")
    suspend fun changeState(taskId: String, newSate: SyncTask.State)

    suspend fun changeSchedulingState(taskId: String, newState: SimpleState, errorMsg: String = "")

    suspend fun changeExecutionState(taskId: String, newState: SimpleState, errorMsg: String = "")

    suspend fun changeSyncTaskEnabled(taskId: String, isEnabled: Boolean)
}