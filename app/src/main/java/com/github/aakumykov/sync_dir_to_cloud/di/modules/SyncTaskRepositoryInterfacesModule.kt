package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncTaskRepository
import dagger.Module
import dagger.Provides

@Module
class SyncTaskRepositoryInterfacesModule {

    @Provides
    fun provideSyncTaskReader(syncTaskRepository: SyncTaskRepository): SyncTaskReader {
        return syncTaskRepository
    }

    @Provides
    fun provideSyncTaskUpdater(syncTaskRepository: SyncTaskRepository): SyncTaskUpdater {
        return syncTaskRepository
    }

    @Provides
    fun provideSyncTaskCreatorDeleter(syncTaskRepository: SyncTaskRepository): SyncTaskCreatorDeleter {
        return syncTaskRepository
    }


}