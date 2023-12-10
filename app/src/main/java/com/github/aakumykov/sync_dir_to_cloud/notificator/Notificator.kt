package com.github.aakumykov.sync_dir_to_cloud.notificator

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.config.NotificationsConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncTaskNotificator
import javax.inject.Inject

class Notificator @Inject constructor(
    private val notificationManagerCompat: NotificationManagerCompat,
    private val resources: Resources
)
    : SyncTaskNotificator
{
    init {
//        prepareNotificationChannel()
    }

    fun prepareNotificationChannel() {
        val notificationChannelCompat = NotificationChannelCompat
            .Builder(
                NotificationsConfig.WORK_CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_LOW)
            .setName(string(R.string.NOTIFICATION_CHANNEL_work_channel_name))
            .setDescription(string(R.string.NOTIFICATION_CHANNEL_work_channel_description))
            .build()

        notificationManagerCompat.createNotificationChannel(notificationChannelCompat)
    }

    override fun showNotification(syncTask: SyncTask) {

    }

    override fun hideNotification(syncTask: SyncTask) {

    }

    override fun updateNotification(syncTask: SyncTask) {

    }

    private fun string(@StringRes strRes: Int): String {
        return resources.getString(strRes)
    }
}