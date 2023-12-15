package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.config.NotificationsConfig
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import javax.inject.Inject

class SyncTaskNotificator @Inject constructor(
    @AppContext private val appContext: Context,
    private val notificationManagerCompat: NotificationManagerCompat,
    private val notificationChannelHelper: NotificationChannelHelper,
    private val syncTaskReader: SyncTaskReader
) {
    // TODO: переделать на taskId
    fun showNotification(syncTask: SyncTask) {

        prepareNotificationChannel()

        val id = syncTask.notificationId

        when (syncTask.state) {
            SyncTask.State.IDLE -> showNotificationReal(id, R.string.NOTIFICATION_idle)
            SyncTask.State.READING_SOURCE -> showNotificationReal(id, R.string.NOTIFICATION_reading_source)
            SyncTask.State.WRITING_TARGET -> showNotificationReal(id, R.string.NOTIFICATION_writing_target)
            SyncTask.State.SUCCESS -> showNotificationReal(id, R.string.NOTIFICATION_success)
            SyncTask.State.SEMI_SUCCESS -> showNotificationReal(id, R.string.NOTIFICATION_semi_success)
            SyncTask.State.ERROR -> showNotificationReal(id, R.string.NOTIFICATION_error)
        }
    }

    suspend fun updateNotification(taskId: String) {
        val syncTask = syncTaskReader.getSyncTask(taskId)
        showNotification(syncTask)
    }

    fun hideNotification(syncTask: SyncTask) {
        notificationManagerCompat.cancel(syncTask.notificationId)
    }


    private fun prepareNotificationChannel() {
        if (!notificationChannelHelper.channelExists(NotificationsConfig.CHANNEL_ID)) {
            notificationChannelHelper.createNotificationChannel(
                NotificationsConfig.CHANNEL_ID,
                NotificationsConfig.CHANNEL_IMPORTANCE,
                string(NotificationsConfig.CHANNEL_NAME_RES),
                string(NotificationsConfig.CHANNEL_DESCRIPTION_RES)
            )
        }
    }


    private fun showNotificationReal(notificationId: Int, @StringRes textRes: Int) {

        val notificationCompat = NotificationCompat.Builder(appContext, NotificationsConfig.CHANNEL_ID).apply {
            setSmallIcon(NotificationsConfig.SMALL_ICON)
            setContentTitle(string(textRes))
            setOngoing(true)
            setProgress(0,0,true)
        }.build()

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

        notificationManagerCompat.notify(
            NotificationsConfig.TAG,
            notificationId,
            notificationCompat
        )
    }


    private fun string(@StringRes strRes: Int): String = appContext.resources.getString(strRes)
}