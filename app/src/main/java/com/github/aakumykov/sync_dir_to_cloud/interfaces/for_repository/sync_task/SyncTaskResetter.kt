package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskResetter {
    suspend fun resetSyncTask(taskId: String): Result<SyncTask>
}