package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_notifications

interface SyncTaskNotificationShower {
    suspend fun showNotification(taskId: String)
}