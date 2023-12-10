package com.github.aakumykov.sync_dir_to_cloud.interfaces

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskNotificator {
    fun showNotification(syncTask: SyncTask)
    fun hideNotification(syncTask: SyncTask)
    fun updateNotification(syncTask: SyncTask)
}