package com.github.aakumykov.sync_dir_to_cloud.di.modules

import dagger.Module
import dagger.Provides

@Module
class StarterStopperModule {

    @Provides
    fun provideSyncTaskStarter(syncTaskStarterStopper: com.github.aakumykov.sync_dir_to_cloud.workers.SyncTaskStarterStopper): com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper {
        return syncTaskStarterStopper
    }
}