package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

interface SyncTaskRunningTimeUpdater {
    suspend fun updateStartTime(taskId: String)
    suspend fun updateFinishTime(taskId: String)
    suspend fun clearFinishTime(taskId: String)
}