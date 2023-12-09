package com.github.aakumykov.sync_dir_to_cloud.interfaces

import com.github.aakumykov.entities.SyncTask

interface SyncTaskNotificator {
    fun showNotification(syncTask: SyncTask)
    fun updateNotification(taskId: String, newSyncTask: SyncTask)
    fun hideNotification(taskId: String)
}