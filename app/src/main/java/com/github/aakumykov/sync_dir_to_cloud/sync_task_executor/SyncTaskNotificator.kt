package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.config.ProgressNotificationsConfig
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.tagWithHashCode
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.github.aakumykov.sync_dir_to_cloud.utils.NotificationChannelHelper
import com.github.aakumykov.sync_dir_to_cloud.view.MainActivity
import com.github.aakumykov.sync_dir_to_cloud.view.task_state.TaskStateFragment
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
            setContentTitle(string(R.string.NOTIFICATION_title))
//            setContentInfo(":-) - content info")
//            setSubText(";-) sub text")
        }
    }


    fun showNotification(taskId: String, notificationId: Int, state: SyncTask.State) {

        MyLogger.d(tagWithHashCode(), "showNotification($taskId, $notificationId, $state)")

        prepareNotificationChannel()

        // FIXME: отдельный статус для этого процесса
        when (state) {
            SyncTask.State.IDLE -> showNotificationReal(taskId, notificationId, R.string.NOTIFICATION_idle)
            SyncTask.State.READING_SOURCE -> showNotificationReal(taskId, notificationId, R.string.NOTIFICATION_reading_source)
            SyncTask.State.WRITING_TARGET -> showNotificationReal(taskId, notificationId, R.string.NOTIFICATION_writing_target)
            SyncTask.State.SEMI_SUCCESS -> showNotificationReal(taskId, notificationId, R.string.NOTIFICATION_semi_success)
            // FIXME: ошибочное уведомление должно быть скрываемым
            SyncTask.State.EXECUTION_ERROR -> showNotificationReal(taskId, notificationId, R.string.NOTIFICATION_error)
            SyncTask.State.SUCCESS -> showNotificationReal(taskId, notificationId, R.string.NOTIFICATION_success)
            // В этом состоянии уведомления быть не должно.
            SyncTask.State.SCHEDULING_ERROR -> {}
        }
    }

    fun hideNotification(taskId: String, notificationId: Int) {
        MyLogger.d(tagWithHashCode(), "hideNotification($taskId, $notificationId)")
        notificationManagerCompat.cancel(taskId, notificationId)
    }

    private fun showNotificationReal(taskId: String ,notificationId: Int, @StringRes textRes: Int) {

        // Проверка наличия разрешения на уведомления
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            MyLogger.e(TAG, "Нет разрешения на показ уведомлений.")
            return
        }

        notificationBuilder
            .apply {
                setContentText(string(textRes))
                setContentIntent(contentPendingIntent(taskId))
            }
            .also {
                notificationManagerCompat.notify(taskId, notificationId, it.build())
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


    // FIXME: для запуска Activity нежелательно использовать ApplicationContext ...
    private fun contentPendingIntent(taskId: String): PendingIntent {
        return PendingIntent.getActivity(
            appContext,
            CODE_SHOW_TASK_STATE,
            intent(taskId),
            flags()
        )
    }

    private fun intent(taskId: String): Intent {
        return Intent(appContext, MainActivity::class.java).apply {
            action = MainActivity.ACTION_SHOW_TASK_STATE
            putExtra(TaskStateFragment.KEY_TASK_ID, taskId)
        }
    }

    private fun flags(): Int = PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE


    companion object {
        val TAG: String = SyncTaskNotificator::class.java.simpleName
        const val CODE_SHOW_TASK_STATE: Int = 10
    }
}