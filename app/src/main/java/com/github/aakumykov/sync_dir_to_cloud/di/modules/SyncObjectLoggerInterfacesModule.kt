package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogReader
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectLogRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface SyncObjectLoggerInterfacesModule {

    @Binds
    fun bindSyncObjectLogDeleter(syncObjectLogRepository: SyncObjectLogRepository): SyncObjectLogDeleter


    @Binds
    fun bindSyncObjectLogReader(syncObjectLogRepository: SyncObjectLogRepository): SyncObjectLogReader
}