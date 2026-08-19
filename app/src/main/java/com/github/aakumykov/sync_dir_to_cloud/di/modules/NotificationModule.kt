package com.github.aakumykov.sync_dir_to_cloud.di.modules

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.config.NotificationChannelConfig
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificationUpdater
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import dagger.Module
import dagger.Provides

@Module
class NotificationModule {

    @Provides
    @AppScope
    fun provideNotificationManager(@AppContext context: Context): NotificationManagerCompat
        = NotificationManagerCompat.from(context)

    @Provides
    fun provideNotificationChannelsConfig(): NotificationChannelConfig {
        return NotificationChannelConfig
    }

    @Provides
    fun provideSyncTaskNotificationUpdater(syncTaskNotificator: SyncTaskNotificator): SyncTaskNotificationUpdater {
        return syncTaskNotificator
    }
}
