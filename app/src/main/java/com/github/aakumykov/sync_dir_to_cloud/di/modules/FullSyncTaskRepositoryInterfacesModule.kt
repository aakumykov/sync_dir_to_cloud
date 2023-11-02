package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.FullSyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.FullSyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.FullSyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.FullSyncTaskRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncTaskRepository
import dagger.Module
import dagger.Provides

@Module
class FullSyncTaskRepositoryInterfacesModule {

    @Provides
    fun provideSyncTaskReader(fullSyncTaskRepository: FullSyncTaskRepository): FullSyncTaskReader {
        return fullSyncTaskRepository
    }

    @Provides
    fun provideSyncTaskUpdater(fullSyncTaskRepository: FullSyncTaskRepository): FullSyncTaskUpdater {
        return fullSyncTaskRepository
    }

    @Provides
    fun provideSyncTaskCreatorDeleter(fullSyncTaskRepository: FullSyncTaskRepository): FullSyncTaskCreatorDeleter {
        return fullSyncTaskRepository
    }


}