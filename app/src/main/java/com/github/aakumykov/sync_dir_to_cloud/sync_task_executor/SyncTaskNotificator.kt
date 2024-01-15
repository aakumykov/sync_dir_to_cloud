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
import com.github.aakumykov.sync_dir_to_cloud.config.NotificationsConfig
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskState
import com.github.aakumykov.sync_dir_to_cloud.utils.NotificationChannelHelper
import javax.inject.Inject

class SyncTaskNotificator @Inject constructor(
    @AppContext private val appContext: Context,
    private val notificationManagerCompat: NotificationManagerCompat,
    private val notificationChannelHelper: NotificationChannelHelper
) {
    private var notificationBuilder: NotificationCompat.Builder? = null


    fun showNotification(taskState: TaskState) {
        Log.d(TAG, "showNotification(hc: ${hashCode()}), $taskState")

        prepareNotificationChannel()

        when (taskState.state) {
            SyncTask.State.IDLE -> showNotificationReal(taskState.notificationId, R.string.NOTIFICATION_idle)
            SyncTask.State.READING_SOURCE -> showNotificationReal(taskState.notificationId, R.string.NOTIFICATION_reading_source)
            SyncTask.State.WRITING_TARGET -> showNotificationReal(taskState.notificationId, R.string.NOTIFICATION_writing_target)
            SyncTask.State.SEMI_SUCCESS -> showNotificationReal(taskState.notificationId, R.string.NOTIFICATION_semi_success)
            // FIXME: ошибочное уведомление должно быть скрываемым
            SyncTask.State.ERROR -> showNotificationReal(taskState.notificationId, R.string.NOTIFICATION_error)
            SyncTask.State.SUCCESS -> showNotificationReal(taskState.notificationId, R.string.NOTIFICATION_success)
        }
    }

    fun hideNotification(notificationId: Int) {
        notificationManagerCompat.cancel(notificationId)
    }

    private fun showNotificationReal(notificationId: Int, @StringRes textRes: Int) {

        if (null == notificationBuilder) {
            notificationBuilder = NotificationCompat.Builder(appContext, NotificationsConfig.CHANNEL_ID).apply {
                setSmallIcon(NotificationsConfig.SMALL_ICON)
                setContentTitle(string(textRes))
                setOngoing(true)
                setProgress(0,0,true)
            }
        }

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

        notificationBuilder?.let {
            notificationManagerCompat.notify(
                NotificationsConfig.TAG,
                notificationId,
                it.build()
            )
        }
    }


    private fun prepareNotificationChannel() {
        deleteNotificationsChannel()

        if (!notificationChannelHelper.channelExists(NotificationsConfig.CHANNEL_ID)) {
            notificationChannelHelper.createNotificationChannel(
                NotificationsConfig.CHANNEL_ID,
                NotificationsConfig.CHANNEL_IMPORTANCE,
                string(NotificationsConfig.CHANNEL_NAME_RES),
                string(NotificationsConfig.CHANNEL_DESCRIPTION_RES)
            )
        }
    }


    private fun deleteNotificationsChannel() {
        notificationManagerCompat.deleteNotificationChannel(NotificationsConfig.CHANNEL_ID)
    }


    private fun string(@StringRes strRes: Int): String = appContext.resources.getString(strRes)


    companion object {
        val TAG: String = SyncTaskNotificator::class.java.simpleName
    }

}