package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskReader {
    suspend fun getSyncTask(id: String): SyncTask
    suspend fun listSyncTasks(): LiveData<List<SyncTask>>
}