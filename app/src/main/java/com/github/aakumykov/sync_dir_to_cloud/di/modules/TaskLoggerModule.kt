package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.repository.TaskLogRepository
import com.github.aakumykov.sync_dir_to_cloud.sync_task_logger.DatabaseSyncTaskLogger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_logger.SyncTaskLogger
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
class TaskLoggerModule {

    /*@Binds
    fun bindSyncTaskLogger(taskLogRepository: TaskLogRepository): SyncTaskLogger
        = DatabaseSyncTaskLogger(taskLogRepository)*/

    @Provides
    fun provideDatabaseSyncTaskLogger(taskLogRepository: TaskLogRepository): SyncTaskLogger {
        return DatabaseSyncTaskLogger(taskLogRepository)
    }
}