package com.github.aakumykov.sync_dir_to_cloud.notificator

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.config.NotificationChannelConfig
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncTaskNotificator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
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
            .setContentText("${syncTask.sourcePath} --> ${syncTask.targetPath}")
            .setSmallIcon(R.drawable.ic_sync_task_notification_progress)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setUsesChronometer(true)
    }

    private val successNotificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(appContext, notificationChannelConfig.success.channelId)
            .setContentTitle(getString(R.string.sync_task_success_notification_title))
            .setContentText("${syncTask.sourcePath} --> ${syncTask.targetPath}")
            .setSmallIcon(R.drawable.ic_sync_task_notification_success)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setUsesChronometer(true)
    }

    private val errorNotificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(appContext, notificationChannelConfig.error.channelId)
            .setContentTitle(getString(R.string.sync_task_error_notification_title))
            .setSmallIcon(R.drawable.ic_sync_task_notification_error)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setUsesChronometer(true)
    }

    @SuppressLint("MissingPermission")
    fun showProgressNotification() {
        syncTaskNotificationChannelHelper.createProgressNotificationChannelItNotExists()
        val id = newNotificationId
        notificationManagerCompat.notify(id, progressNotificationBuilder.build())
        progressNotificationId = id
    }

    fun hideProgressNotification() {
        progressNotificationId?.also {
            notificationManagerCompat.cancel(it)
        }
    }

    @SuppressLint("MissingPermission")
    fun showSuccessNotification() {
        syncTaskNotificationChannelHelper.createSuccessNotificationChannelItNotExists()
        notificationManagerCompat.notify(newNotificationId, successNotificationBuilder.build())
    }


    @SuppressLint("MissingPermission")
    fun showErrorNotification(throwable: Throwable) {
        syncTaskNotificationChannelHelper.createErrorNotificationChannelItNotExists()
        notificationManagerCompat.notify(
            newNotificationId,
            errorNotificationBuilder
                .setContentText(throwable.errorMsgExtended)
                .build()
        )
    }

    private fun getString(stringRes: Int): String = appContext.getString(stringRes)
}


@AssistedFactory
interface SyncTaskNotificatorAssistedFactory {
    fun create(syncTask: SyncTask): SyncTaskNotificator
}