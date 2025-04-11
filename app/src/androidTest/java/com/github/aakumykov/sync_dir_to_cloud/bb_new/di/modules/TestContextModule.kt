package com.github.aakumykov.sync_dir_to_cloud.bb_new.di.modules

import android.content.Context
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import dagger.Module
import dagger.Provides

@Module
class TestContextModule(private val appContext: Context) {

    @AppScope
    @Provides
    fun provideAppContext(): Context {
        return appContext
    }
}