package com.github.aakumykov.sync_dir_to_cloud.di

import android.content.Context
import android.content.res.Resources
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import dagger.Module
import dagger.Provides

@Module
class ResourcesModule {

    @AppScope
    @Provides
    fun provideAppResources(@AppContext appContext: Context): Resources {
        return appContext.resources
    }
}
