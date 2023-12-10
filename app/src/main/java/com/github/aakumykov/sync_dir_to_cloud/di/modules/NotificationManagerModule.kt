package com.github.aakumykov.sync_dir_to_cloud.di.modules

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import dagger.Module
import dagger.Provides

@Module
class NotificationManagerModule (
//    private val notificationManagerCompat: NotificationManagerCompat
    private val appContext: Context
) {
    @AppScope
    @Provides
    fun provideNotificationManager(): NotificationManagerCompat {
        return NotificationManagerCompat.from(appContext);
    }
}