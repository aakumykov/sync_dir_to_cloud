package com.github.aakumykov.sync_dir_to_cloud.di.modules

import dagger.Module
import dagger.Provides
import dagger.assisted.Assisted
import javax.inject.Named

@Module
class DirListerModule {

    @Provides
    @Named()
    fun provideDirLister():

    @Provides
    fun provideRecursiveDirReader(@Assisted) {

    }
}
