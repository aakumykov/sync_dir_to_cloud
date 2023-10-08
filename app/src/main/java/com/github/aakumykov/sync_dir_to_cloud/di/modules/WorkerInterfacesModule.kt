package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper
import com.github.aakumykov.sync_dir_to_cloud.workers.WorkManagerSyncTaskScheduler
import com.github.aakumykov.sync_dir_to_cloud.workers.WorkManagerSyncTaskStarterStopper
import dagger.Module
import dagger.Provides

@Module
class WorkerInterfacesModule {

    @Provides
    fun provideSyncTaskStarterStopper(workManagerSyncTaskStarterStopper: WorkManagerSyncTaskStarterStopper): SyncTaskStarterStopper {
        return workManagerSyncTaskStarterStopper
    }

    @Provides
    fun provideSyncTaskScheduler(workManagerSyncTaskScheduler: WorkManagerSyncTaskScheduler): SyncTaskScheduler {
        return workManagerSyncTaskScheduler
    }
}