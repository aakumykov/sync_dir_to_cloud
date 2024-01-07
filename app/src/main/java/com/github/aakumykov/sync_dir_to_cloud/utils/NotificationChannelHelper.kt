package com.github.aakumykov.sync_dir_to_cloud.utils

import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import javax.inject.Inject

class NotificationChannelHelper @Inject constructor(private val notificationManagerCompat: NotificationManagerCompat) {

    fun createNotificationChannel(
        id: String,
        importance: Int,
        name: String,
        description: String? = null
    ) {

        val notificationChannelCompat = NotificationChannelCompat.Builder(id, importance).apply {
            setName(name)
            description?.let { setDescription(description) }
        }.build()

        notificationManagerCompat.createNotificationChannel(notificationChannelCompat)
    }

    fun channelExists(id: String): Boolean {
        return null != notificationManagerCompat.getNotificationChannel(id)
    }
}