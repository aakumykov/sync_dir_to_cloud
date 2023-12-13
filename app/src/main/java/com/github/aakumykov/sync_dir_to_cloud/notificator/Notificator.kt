package com.github.aakumykov.sync_dir_to_cloud.notificator

import android.Manifest
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.PendingIntentCompat
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.config.NotificationsConfig
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncTaskNotificator
import javax.inject.Inject

class Notificator @Inject constructor(
    @AppContext private val appContext: Context,
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

        prepareNotificationChannel()

        val notification = NotificationCompat.Builder(appContext, NotificationsConfig.WORK_CHANNEL_ID)
            .setContentTitle("Запустить подготовку")
            .setContentText("пробно")
            .setSmallIcon(R.drawable.ic_start)
            .addAction(startAction())
            .build()

        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        notificationManagerCompat.notify(1, notification)
    }

    private fun startAction(): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.ic_start,
            string(R.string.NOTIFICATION_ACTION_start),
            startActionPendingIntent())
            .build()
    }

    private fun startActionPendingIntent(): PendingIntent? {
        return PendingIntentCompat.getService(
            appContext,
            SyncTaskExecutionService.CODE_START_WORK,
            Intent(appContext, SyncTaskExecutionService::class.java).apply {
                action = SyncTaskExecutionService.ACTION_START_WORK
            },
            FLAG_CANCEL_CURRENT,
            false
        )
    }

    override fun hideNotification(syncTask: SyncTask) {

    }

    override fun updateNotification(syncTask: SyncTask) {

    }

    private fun string(@StringRes strRes: Int): String {
        return resources.getString(strRes)
    }

}