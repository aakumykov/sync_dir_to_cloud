package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.repository.sync_operation_log_repository.SyncOperationLogReader
import com.github.aakumykov.sync_dir_to_cloud.repository.sync_operation_log_repository.FileOperationLogRepository
import dagger.Module
import dagger.Provides

@Module
class SyncOperationLogRepositoryInterfacesModule {

    @Provides
    fun provideSyncOperationLogReader(fileOperationLogRepository: FileOperationLogRepository): SyncOperationLogReader {
        return fileOperationLogRepository
    }
}
