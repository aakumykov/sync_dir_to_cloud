package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.job_holdes.OperationJobsHolder
import dagger.Module
import dagger.Provides

@Module
class TempModule {

    @Provides
    fun provideOperationJobsHolder() = OperationJobsHolder
}
