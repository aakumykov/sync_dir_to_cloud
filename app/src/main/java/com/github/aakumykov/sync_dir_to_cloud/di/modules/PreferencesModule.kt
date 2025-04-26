package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import dagger.Module
import dagger.Provides

@Module
class PreferencesModule {

    // FIXME: AppPreferences и так Object
    @Provides
    @AppScope
    fun provideAppPreferences(): AppPreferences = AppPreferences
}
