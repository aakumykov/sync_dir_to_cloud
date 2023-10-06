package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStopper
import com.github.aakumykov.sync_dir_to_cloud.workers.SyncTaskStarterStopper
import dagger.Module
import dagger.Provides

@Module
class StarterStopperModule {

    @Provides
    fun provideSyncTaskStarter(syncTaskStarterStopper: SyncTaskStarterStopper): SyncTaskStarter {
        return syncTaskStarterStopper
    }

    @Provides
    fun provideSyncTaskStopper(syncTaskStarterStopper: SyncTaskStarterStopper): SyncTaskStopper {
        return syncTaskStarterStopper
    }
}