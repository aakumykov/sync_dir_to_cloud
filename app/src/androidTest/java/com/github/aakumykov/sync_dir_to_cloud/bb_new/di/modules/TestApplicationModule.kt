package com.github.aakumykov.sync_dir_to_cloud.bb_new.di.modules

import android.app.Application
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import dagger.Module
import dagger.Provides

@Module
class TestApplicationModule(private val application: Application) {

    @Provides
    @AppScope
    fun provideApplication(): Application {
        return this.application
    }
}