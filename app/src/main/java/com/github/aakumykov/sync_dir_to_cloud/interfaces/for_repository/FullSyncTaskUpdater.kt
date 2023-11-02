package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FullSyncTask

interface FullSyncTaskUpdater {
    suspend fun updateFullSyncTask(fullSyncTask: FullSyncTask)
}