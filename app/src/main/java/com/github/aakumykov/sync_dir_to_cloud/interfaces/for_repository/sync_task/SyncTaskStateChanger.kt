package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskStateChanger {
    suspend fun changeState(taskId: String, newSate: SyncTask.State)
    suspend fun changeSchedulingState(taskId: String, newSate: SyncTask.SimpleState, errorMsg: String = "")
}