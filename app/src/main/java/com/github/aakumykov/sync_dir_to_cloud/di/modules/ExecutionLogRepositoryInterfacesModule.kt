package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import com.github.aakumykov.sync_dir_to_cloud.repository.ExecutionLogRepository
import dagger.Binds
import dagger.Module

@Module
interface ExecutionLogRepositoryInterfacesModule {

    @Binds
    fun bindExecutionLogger(executionLogRepository: ExecutionLogRepository): ExecutionLogger
}