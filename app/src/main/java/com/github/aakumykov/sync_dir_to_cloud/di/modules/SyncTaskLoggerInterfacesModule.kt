package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log.SyncTaskLogDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log.TaskStateLogger
import com.github.aakumykov.sync_dir_to_cloud.repository.TaskLogRepository
import dagger.Binds
import dagger.Module

@Module
interface SyncTaskLoggerInterfacesModule {

    @Binds
    fun bindSyncTaskLogDeleter(taskLogRepository: TaskLogRepository): SyncTaskLogDeleter

    @Binds
    fun bindTaskStateLogger(taskLogRepository: TaskLogRepository): TaskStateLogger
}