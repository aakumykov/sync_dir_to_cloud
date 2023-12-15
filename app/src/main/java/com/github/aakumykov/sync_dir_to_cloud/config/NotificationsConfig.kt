package com.github.aakumykov.sync_dir_to_cloud.config

import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.R

class NotificationsConfig {
    companion object {
        const val CHANNEL_ID: String = "SYNC_PROCESS_NOTIFICATIONS_CHANNEL"
        const val CHANNEL_IMPORTANCE: Int = NotificationManagerCompat.IMPORTANCE_LOW
        val CHANNEL_NAME_RES: Int = R.string.notifications_channel_name
        val CHANNEL_DESCRIPTION_RES: Int = R.string.notifications_channel_description
        val SMALL_ICON: Int = R.drawable.ic_notification
        val TAG: String = "NOTIFICATIONS_TAG"
    }
}
