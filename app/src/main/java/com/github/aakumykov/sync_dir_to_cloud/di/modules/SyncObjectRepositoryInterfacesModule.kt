package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncTaskRepository
import dagger.Module
import dagger.Provides

@Module
class SyncObjectRepositoryInterfacesModule {

    @Provides
    fun provideSyncObjectAdder(syncObjectRepository: SyncObjectRepository): SyncObjectAdder {
        return syncObjectRepository
    }

    @Provides
    fun provideSyncTaskStateChanger(syncTaskRepository: SyncTaskRepository): SyncTaskStateChanger {
        return syncTaskRepository
    }

    @Provides
    fun provideSyncObjectReader(syncObjectRepository: SyncObjectRepository): SyncObjectReader {
        return syncObjectRepository
    }
}