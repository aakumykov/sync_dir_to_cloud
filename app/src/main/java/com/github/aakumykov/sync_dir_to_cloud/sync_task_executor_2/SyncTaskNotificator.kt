package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2

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
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_notifications.SyncTaskNotificationHider
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_notifications.SyncTaskNotificationShower
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.utils.NotificationChannelHelper
import javax.inject.Inject

class SyncTaskNotificator @Inject constructor(
    @AppContext private val appContext: Context,
    private val notificationManagerCompat: NotificationManagerCompat,
    private val notificationChannelHelper: NotificationChannelHelper,
    private val syncTaskReader: SyncTaskReader
)
    : SyncTaskNotificationShower, SyncTaskNotificationHider
{
    private var notificationBuilder: NotificationCompat.Builder? = null

    override suspend fun showNotification(taskId: String) {

        deleteNotificationsChannel() // FIXME: временно!
        prepareNotificationChannel()

        val syncTask = syncTaskReader.getSyncTask(taskId)

        val notificationId = syncTask.notificationId

        Log.d(TAG, "showNotification(${syncTask.state})")

        when (syncTask.state) {
            SyncTask.State.IDLE -> showNotificationReal(notificationId, R.string.NOTIFICATION_idle)
            SyncTask.State.READING_SOURCE -> showNotificationReal(notificationId, R.string.NOTIFICATION_reading_source)
            SyncTask.State.WRITING_TARGET -> showNotificationReal(notificationId, R.string.NOTIFICATION_writing_target)
            SyncTask.State.SUCCESS -> showNotificationReal(notificationId, R.string.NOTIFICATION_success)
            SyncTask.State.SEMI_SUCCESS -> showNotificationReal(notificationId, R.string.NOTIFICATION_semi_success)
            SyncTask.State.ERROR -> showNotificationReal(notificationId, R.string.NOTIFICATION_error)
        }
    }

    private fun deleteNotificationsChannel() {
        notificationManagerCompat.deleteNotificationChannel(NotificationsConfig.CHANNEL_ID)
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


    override fun hideNotification(notificationId: Int) {
        notificationManagerCompat.cancel(NotificationsConfig.TAG, notificationId)
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


    private fun string(@StringRes strRes: Int): String = appContext.resources.getString(strRes)


    companion object {
        val TAG: String = SyncTaskNotificator::class.java.simpleName
    }
}