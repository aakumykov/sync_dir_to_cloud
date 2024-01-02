package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.google.gson.Gson
import dagger.Module
import dagger.Provides

@Module
class GsonModule {

    @Provides
    fun provideGSon() = Gson()
}