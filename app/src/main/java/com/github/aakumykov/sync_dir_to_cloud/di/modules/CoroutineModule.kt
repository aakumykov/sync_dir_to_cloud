package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.MainCoroutineScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope

@Module
class CoroutineModule {

    @Provides
    @MainCoroutineScope
    fun provideMainCoroutineScope(): CoroutineScope = MainScope()

    @Provides
    fun provideJob(): Job {
        return Job()
    }

    @Provides
    fun provideCoroutineScope(job: Job): CoroutineScope {
        return CoroutineScope(job)
    }

    @AppScope
    @DispatcherIO
    @Provides
    fun provideDispatchersIO(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}