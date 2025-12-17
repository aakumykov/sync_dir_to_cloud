package com.github.aakumykov.sync_dir_to_cloud.utils

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import javax.inject.Inject

class NotificationChannelHelper @Inject constructor(
    private val notificationManagerCompat: NotificationManagerCompat
) {
    constructor(context: Context) : this(NotificationManagerCompat.from(context))


    fun createChannel(
        channelId: String,
        channelName: String,
        channelDescription: String? = null,
        channelImportance: Int,
    ): Boolean {

        val notificationChannelCompat = NotificationChannelCompat.Builder(channelId, channelImportance).apply {
            setName(channelName)
            channelDescription?.let { setDescription(channelDescription) }
        }.build()

        notificationManagerCompat.createNotificationChannel(notificationChannelCompat)

        return channelExists(channelId)
    }


    fun createChannelIfNotExists(
        channelId: String,
        channelName: String,
        channelDescription: String? = null,
        channelImportance: Int,
    ): Boolean {
        return if (!channelExists(channelId))
            createChannel(channelId, channelName, channelDescription, channelImportance)
        else true
    }


    fun reCreateChannel(
        channelId: String,
        channelName: String,
        channelDescription: String? = null,
        channelImportance: Int,
    ): Boolean {
        deleteChannel(channelId)
        return createChannel(channelId, channelName, channelDescription, channelImportance)
    }


    fun deleteChannel(channelId: String): Boolean {
        return if (channelExists(channelId)) {
            notificationManagerCompat.deleteNotificationChannel(channelId)
            return !channelExists(channelId)
        } else false
    }


    fun channelExists(id: String): Boolean {
        return null != notificationManagerCompat.getNotificationChannel(id)
    }
}