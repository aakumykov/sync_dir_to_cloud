package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper
import com.github.aakumykov.sync_dir_to_cloud.workers.SyncTaskStarterStopperStopper
import dagger.Module
import dagger.Provides

@Module
class StarterStopperModule {

    @Provides
    fun provideSyncTaskStarter(syncTaskStarterStopper: SyncTaskStarterStopperStopper): SyncTaskStarterStopper {
        return syncTaskStarterStopper
    }

    @Provides
    fun provideSyncTaskStopper(syncTaskStarterStopper: SyncTaskStarterStopperStopper): SyncTaskStopper {
        return syncTaskStarterStopper
    }
}