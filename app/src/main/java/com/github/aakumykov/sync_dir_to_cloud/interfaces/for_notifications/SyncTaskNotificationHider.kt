package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_notifications

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskNotificationHider {
    fun hideNotification(syncTask: SyncTask)
}