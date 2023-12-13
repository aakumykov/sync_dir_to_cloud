package com.github.aakumykov.sync_dir_to_cloud.di.modules

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import dagger.Module
import dagger.Provides

@Module
class NotificationManagerModule {

    @Provides
    @AppScope
    fun provideNotificationManager(@AppContext context: Context): NotificationManagerCompat
        = NotificationManagerCompat.from(context)
}
