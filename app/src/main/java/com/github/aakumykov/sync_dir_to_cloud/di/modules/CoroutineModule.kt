package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.CoroutineFileOperationJob
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.CoroutineFileCopyingScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.CoroutineMainScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job

@Module
class CoroutineModule {

    // TODO: "DefaultJob"
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

    @Provides
    @CoroutineFileCopyingScope
    fun provideFileCopyingScope(@DispatcherIO ioDispatcher: CoroutineDispatcher): CoroutineScope
        = CoroutineScope(ioDispatcher + Job())

    // TODO: Главный Job делать руками?

    @Provides
    @CoroutineFileOperationJob
    fun provideFileOperationJob(@CoroutineFileCopyingScope fileCopyingScope: CoroutineScope): CompletableJob {
        return SupervisorJob(fileCopyingScope.coroutineContext.job)
    }
}