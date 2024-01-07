package com.github.aakumykov.sync_dir_to_cloud.di.modules

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_notifications.SyncTaskNotificationHider
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_notifications.SyncTaskNotificationShower
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.SyncTaskNotificator
import dagger.Module
import dagger.Provides

@Module
class NotificationModule {

    @Provides
    @AppScope
    fun provideNotificationManager(@AppContext context: Context): NotificationManagerCompat
        = NotificationManagerCompat.from(context)


    @Provides
    fun provideSyncTaskNotificationShower(syncTaskNotificator: SyncTaskNotificator): SyncTaskNotificationShower {
        return syncTaskNotificator
    }


    @Provides
    fun provideSyncTaskNotificationHider(syncTaskNotificator: SyncTaskNotificator): SyncTaskNotificationHider {
        return syncTaskNotificator
    }
}
