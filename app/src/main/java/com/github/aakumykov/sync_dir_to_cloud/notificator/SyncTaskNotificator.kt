package com.github.aakumykov.sync_dir_to_cloud.notificator

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import com.github.aakumykov.sync_dir_to_cloud.GlobalKeys.KEY_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.GlobalKeys.KEY_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.config.NotificationChannelConfig
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.view.MainActivity
import javax.inject.Inject

class SyncTaskNotificator @Inject constructor(
    @param:AppContext private val appContext: Context,
    private val notificationManagerCompat: NotificationManagerCompat,
    private val syncTaskNotificationChannelHelper: SyncTaskNotificationChannelHelper,
    private val notificationChannelConfig: NotificationChannelConfig
) {
    private val newNotificationId: Int get() = View.generateViewId()
    private var progressNotificationId: Int? = null

    private val progressNotificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(appContext, notificationChannelConfig.progress.channelId)
            .setContentTitle(getString(R.string.sync_task_progress_notification_title))
            .setSmallIcon(R.drawable.ic_sync_task_notification_progress)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setUsesChronometer(true)
//            .setContentIntent(SyncLogFragment.pendingIntent())
    }

    private val successNotificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(appContext, notificationChannelConfig.success.channelId)
            .setContentTitle(getString(R.string.sync_task_success_notification_title))
            .setSmallIcon(R.drawable.ic_sync_task_notification_success)
    }

    private val errorNotificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(appContext, notificationChannelConfig.error.channelId)
            .setContentTitle(getString(R.string.sync_task_error_notification_title))
            .setSmallIcon(R.drawable.ic_sync_task_notification_error)
    }

    @SuppressLint("MissingPermission")
    fun showProgressNotification(syncTask: SyncTask, executionId: String) {
        syncTaskNotificationChannelHelper.createProgressNotificationChannelItNotExists()

        val id = newNotificationId

        notificationManagerCompat.notify(
            id,
            progressNotificationBuilder
                .setContentText("${syncTask.sourcePath} --> ${syncTask.targetPath}")
                .setContentIntent(pendingIntentForSyncLog(syncTask.id, executionId))
                .build()
        )

        progressNotificationId = id
    }

    fun hideProgressNotification() {
        progressNotificationId?.also {
            notificationManagerCompat.cancel(it)
        }
    }

    @SuppressLint("MissingPermission")
    fun showSuccessNotification(syncTask: SyncTask, executionId: String) {
        syncTaskNotificationChannelHelper.createSuccessNotificationChannelItNotExists()

        notificationManagerCompat.notify(
            newNotificationId,
            successNotificationBuilder
                .setContentText("${syncTask.sourcePath} --> ${syncTask.targetPath}")
                .setContentIntent(pendingIntentForSyncLog(syncTask.id, executionId))
                .build()
        )
    }


    @SuppressLint("MissingPermission")
    fun showErrorNotification(throwable: Throwable, syncTask: SyncTask, executionId: String) {
        syncTaskNotificationChannelHelper.createErrorNotificationChannelItNotExists()

        notificationManagerCompat.notify(
            newNotificationId,
            errorNotificationBuilder
                .setContentText(throwable.errorMsgExtended)
                .setContentIntent(pendingIntentForSyncLog(syncTask.id, executionId))
                .build()
        )
    }


    private fun pendingIntentForSyncLog(taskId: String, executionId: String): PendingIntent {

        val arguments = bundleOf().apply {
            putString(KEY_TASK_ID, taskId)
            putString(KEY_EXECUTION_ID, executionId)
        }

        return MainActivity.pendingIntentWithAction(
            appContext,
            MainActivity.ACTION_SHOW_SYNC_LOG,
            arguments
        )
    }

    private fun getString(stringRes: Int): String = appContext.getString(stringRes)

    companion object {
        val TAG: String = SyncTaskNotificator::class.java.simpleName
    }
}