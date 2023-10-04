package com.github.aakumykov.sync_dir_to_cloud.interfaces

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface iSyncTaskUpdater {
    suspend fun updateSyncTask(syncTask: SyncTask)
}