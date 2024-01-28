package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskCreatorDeleter {
    suspend fun createSyncTask(syncTask: SyncTask)
    suspend fun deleteSyncTask(syncTask: SyncTask)
    suspend fun deleteSyncTask(taskId: String)
}