package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectRepository
import dagger.Module
import dagger.Provides

@Module
class SyncObjectRepositoryInterfacesModule {

    @Provides
    fun provideSyncObjectAdder(syncObjectRepository: SyncObjectRepository): SyncObjectAdder {
        return syncObjectRepository
    }
}