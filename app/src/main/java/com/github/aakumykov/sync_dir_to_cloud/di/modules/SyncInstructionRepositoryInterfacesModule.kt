package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import dagger.Module
import dagger.Provides

@Module
class SyncInstructionRepositoryInterfacesModule {

    @Provides
    fun provideSyncInstructionUpdater(syncInstructionRepository: SyncInstructionRepository): SyncInstructionUpdater {
        return syncInstructionRepository
    }
}
