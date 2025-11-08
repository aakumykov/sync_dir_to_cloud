package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.app_settings.AppSettingsReader
import com.github.aakumykov.sync_dir_to_cloud.app_settings.AppSettingsReaderImpl
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import dagger.Module
import dagger.Provides

@Module
class AppSettingsModule {

    @AppScope
    @Provides
    fun provideAppSettings(appSettingsImpl: AppSettingsReaderImpl): AppSettingsReader {
        return appSettingsImpl
    }
}