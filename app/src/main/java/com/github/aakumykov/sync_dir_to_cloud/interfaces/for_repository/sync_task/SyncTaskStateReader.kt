package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import kotlinx.coroutines.flow.Flow

interface SyncTaskStateReader {
    suspend fun getSyncTaskState(taskId: String): Flow<SyncTask.State>
}