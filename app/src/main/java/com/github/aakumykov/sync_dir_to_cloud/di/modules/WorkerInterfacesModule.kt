package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper
import com.github.aakumykov.sync_dir_to_cloud.service.ServiceSyncTaskStarterStopper
import com.github.aakumykov.sync_dir_to_cloud.workers.WorkManagerSyncTaskScheduler
import com.github.aakumykov.sync_dir_to_cloud.workers.WorkManagerSyncTaskStarterStopper
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class WorkerInterfacesModule {

    @Provides
    @Named("worker")
    fun provideWorkManagerSyncTaskStarterStopper(workManagerSyncTaskStarterStopper: WorkManagerSyncTaskStarterStopper): SyncTaskStarterStopper {
        return workManagerSyncTaskStarterStopper
    }

    @Provides
    @Named("service")
    fun provideServiceSyncTaskStarterStopper(serviceSyncTaskStarterStopper: ServiceSyncTaskStarterStopper): SyncTaskStarterStopper {
        return serviceSyncTaskStarterStopper
    }

    @Provides
    fun provideSyncTaskScheduler(workManagerSyncTaskScheduler: WorkManagerSyncTaskScheduler): SyncTaskScheduler {
        return workManagerSyncTaskScheduler
    }
}