package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import javax.inject.Inject

class SyncTaskExecutionNotificator @Inject constructor(
    notificationManager: NotificationManagerCompat
) {
    fun showNotification(syncTask: SyncTask) {

    }

    fun hideNotification(syncTask: SyncTask) {

    }

    fun updateNotification(syncTask: SyncTask) {

    }
}
