package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskScheduler {
    suspend fun scheduleSyncTask(syncTask: SyncTask)
    suspend fun unScheduleSyncTask(syncTask: SyncTask)
}