package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
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