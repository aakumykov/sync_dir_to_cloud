package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.entities.SyncTask

interface SyncTaskReader {
    suspend fun getSyncTask(id: String): SyncTask
    suspend fun listSyncTasks(): LiveData<List<SyncTask>>
}