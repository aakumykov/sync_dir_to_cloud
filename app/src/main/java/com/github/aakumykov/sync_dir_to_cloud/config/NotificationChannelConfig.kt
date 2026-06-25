package com.github.aakumykov.sync_dir_to_cloud.config

import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.R

object NotificationChannelConfig {
    data class Data(
        val channelId: String,
        val name: Int,
        val description: Int,
        val importance: Int
    )
    val progress = Data(
        channelId = "SYNC_TASK_PROGRESS_CHANNEL_ID",
        name = R.string.sync_task_progress_notifications_channel_name,
        description = R.string.sync_task_progress_notifications_channel_description,
        importance = NotificationManagerCompat.IMPORTANCE_LOW
    )
    val success = Data(
        channelId = "SYNC_TASK_SUCCESS_CHANNEL_ID",
        name = R.string.sync_task_success_notifications_channel_name,
        description = R.string.sync_task_success_notifications_channel_description,
        importance = NotificationManagerCompat.IMPORTANCE_LOW
    )
    val error = Data(
        channelId = "SYNC_TASK_ERROR_CHANNEL_ID",
        name = R.string.sync_task_error_notifications_channel_name,
        description = R.string.sync_task_error_notifications_channel_description,
        importance = NotificationManagerCompat.IMPORTANCE_LOW
    )
}