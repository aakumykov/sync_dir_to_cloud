package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.config.ProgressNotificationsConfig
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.tagWithHashCode
import com.github.aakumykov.sync_dir_to_cloud.utils.NotificationChannelHelper
import javax.inject.Inject

class SyncTaskNotificator @Inject constructor(
    @AppContext private val appContext: Context,
    private val notificationManagerCompat: NotificationManagerCompat,
    private val notificationChannelHelper: NotificationChannelHelper
) {
    private val notificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(appContext, ProgressNotificationsConfig.CHANNEL_ID).apply {
            setSmallIcon(ProgressNotificationsConfig.SMALL_ICON)
            setOngoing(true)
            setProgress(0,0,true)
        }
    }


    fun showNotification(notificationId: Int, state: SyncTask.State) {

        Log.d(tagWithHashCode(), "showNotification($notificationId, $state)")

        prepareNotificationChannel()

        when (state) {
            SyncTask.State.IDLE -> showNotificationReal(notificationId, R.string.NOTIFICATION_idle)
            SyncTask.State.READING_SOURCE -> showNotificationReal(notificationId, R.string.NOTIFICATION_reading_source)
            SyncTask.State.WRITING_TARGET -> showNotificationReal(notificationId, R.string.NOTIFICATION_writing_target)
            SyncTask.State.SEMI_SUCCESS -> showNotificationReal(notificationId, R.string.NOTIFICATION_semi_success)
            // FIXME: ошибочное уведомление должно быть скрываемым
            SyncTask.State.ERROR -> showNotificationReal(notificationId, R.string.NOTIFICATION_error)
            SyncTask.State.SUCCESS -> showNotificationReal(notificationId, R.string.NOTIFICATION_success)
        }
    }

    fun hideNotification(notificationId: Int) {
        notificationManagerCompat.cancel(notificationId)
    }

    private fun showNotificationReal(notificationId: Int, @StringRes textRes: Int) {

        // Проверка наличия разрешения на уведомления
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        notificationBuilder.apply {
            setContentTitle(string(textRes))
        }.also { notificationBuilder ->
            notificationManagerCompat.notify(
                ProgressNotificationsConfig.TAG,
                notificationId,
                notificationBuilder.build()
            )
        }
    }


    private fun prepareNotificationChannel() {
        deleteNotificationsChannel()

        if (!notificationChannelHelper.channelExists(ProgressNotificationsConfig.CHANNEL_ID)) {
            notificationChannelHelper.createNotificationChannel(
                ProgressNotificationsConfig.CHANNEL_ID,
                ProgressNotificationsConfig.CHANNEL_IMPORTANCE,
                string(ProgressNotificationsConfig.CHANNEL_NAME_RES),
                string(ProgressNotificationsConfig.CHANNEL_DESCRIPTION_RES)
            )
        }
    }


    private fun deleteNotificationsChannel() {
        notificationManagerCompat.deleteNotificationChannel(ProgressNotificationsConfig.CHANNEL_ID)
    }


    private fun string(@StringRes strRes: Int): String = appContext.resources.getString(strRes)


    companion object {
        val TAG: String = SyncTaskNotificator::class.java.simpleName
    }

}