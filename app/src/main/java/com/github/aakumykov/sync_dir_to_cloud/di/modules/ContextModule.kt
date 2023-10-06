package com.github.aakumykov.sync_dir_to_cloud.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val appContext: Context) {

    @Provides
    fun getAppContext(): Context {
        return appContext
    }
}