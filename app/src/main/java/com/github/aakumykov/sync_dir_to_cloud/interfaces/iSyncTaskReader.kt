package com.github.aakumykov.sync_dir_to_cloud.interfaces

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface iSyncTaskReader {
    suspend fun getSyncTask(id: String): SyncTask?
}