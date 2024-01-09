package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.CoroutineMainScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlin.coroutines.CoroutineContext

@Module
class CoroutineModule {

    @Deprecated("Передавать Dispatchers.IO")
    @Provides
    fun provideJob(): Job {
        return Job()
    }

    @Deprecated("Передавать Dispatchers.IO")
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

    @Provides
    @CoroutineMainScope
    fun provideMainCoroutineScope(): CoroutineScope = MainScope()
}