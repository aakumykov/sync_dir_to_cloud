package com.github.aakumykov.sync_dir_to_cloud.di.mo

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import dagger.Module
import dagger.Provides

@Module
class NotificationManagerModule(@AppContext private val appContext: Context) {

    @Provides
    @AppScope
    fun provideNotificationManager() = NotificationManagerCompat.from(appContext)
}
