package com.github.aakumykov.sync_dir_to_cloud.notificator

import android.content.Context
import com.github.aakumykov.sync_dir_to_cloud.config.NotificationChannelConfig
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.utils.NotificationChannelHelper
import javax.inject.Inject

class SyncTaskNotificationChannelHelper @Inject constructor(
    @param:AppContext private val appContext: Context,
    private val channelHelper: NotificationChannelHelper,
    private val notificationChannelConfig: NotificationChannelConfig,
) {
    fun createProgressNotificationChannelItNotExists() {
        channelHelper.createChannelIfNotExists(
            notificationChannelConfig.progress.channelId,
            appContext.getString(notificationChannelConfig.progress.name),
            appContext.getString(notificationChannelConfig.progress.description),
            notificationChannelConfig.progress.importance
        )
    }

    fun createSuccessNotificationChannelItNotExists() {
        channelHelper.createChannelIfNotExists(
            notificationChannelConfig.success.channelId,
            appContext.getString(notificationChannelConfig.success.name),
            appContext.getString(notificationChannelConfig.success.description),
            notificationChannelConfig.success.importance
        )
    }

    fun createErrorNotificationChannelItNotExists() {
        channelHelper.createChannelIfNotExists(
            notificationChannelConfig.error.channelId,
            appContext.getString(notificationChannelConfig.error.name),
            appContext.getString(notificationChannelConfig.error.description),
            notificationChannelConfig.error.importance
        )
    }
}