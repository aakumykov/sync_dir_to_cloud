package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.repository.SyncTaskLogRepository
import com.github.aakumykov.sync_dir_to_cloud.sync_task_logger.DatabaseSyncTaskLogger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_logger.SyncTaskLogger
import dagger.Module
import dagger.Provides

@Module
class TaskLoggerModule {

    /*@Binds
    fun bindSyncTaskLogger(taskLogRepository: SyncTaskLogRepository): SyncTaskLogger
        = DatabaseSyncTaskLogger(taskLogRepository)*/

    @Provides
    fun provideDatabaseSyncTaskLogger(syncTaskLogRepository: SyncTaskLogRepository): SyncTaskLogger {
        return DatabaseSyncTaskLogger(syncTaskLogRepository)
    }
}