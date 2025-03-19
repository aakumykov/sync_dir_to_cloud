package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository6
import dagger.Module
import dagger.Provides

@Module
class SyncInstructionRepositoryInterfacesModule {

    @Provides
    fun provideSyncInstructionUpdater(syncInstructionRepository6: SyncInstructionRepository6): SyncInstructionUpdater {
        return syncInstructionRepository6
    }
}
