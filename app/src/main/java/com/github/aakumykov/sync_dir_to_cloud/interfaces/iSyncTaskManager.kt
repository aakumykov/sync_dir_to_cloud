package com.github.aakumykov.sync_dir_to_cloud.interfaces

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface iSyncTaskManager {
    suspend fun listSyncTasks(): LiveData<List<SyncTask>>
    suspend fun createSyncTask(syncTask: SyncTask)
    suspend fun getSyncTask(id: String): SyncTask?
    suspend fun deleteSyncTask(syncTask: SyncTask)
}