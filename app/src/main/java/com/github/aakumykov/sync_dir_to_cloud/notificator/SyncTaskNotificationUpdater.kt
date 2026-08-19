package com.github.aakumykov.sync_dir_to_cloud.notificator

import androidx.annotation.StringRes

interface SyncTaskNotificationUpdater {
    fun updateProgressNotification(
        message: String,
        notificationId: Int,
        taskId: String,
        executionId: String
    )
    fun updateProgressNotification(
        @StringRes messageId: Int,
        notificationId: Int,
        taskId: String,
        executionId: String
    )
}