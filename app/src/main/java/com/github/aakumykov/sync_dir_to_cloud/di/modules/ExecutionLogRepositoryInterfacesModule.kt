package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogCleaner
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.OperationLogger
import com.github.aakumykov.sync_dir_to_cloud.repository.OperationLogRepository
import dagger.Binds
import dagger.Module

@Module
interface ExecutionLogRepositoryInterfacesModule {

    @Binds
    fun bindExecutionLogger(executionLogRepository: OperationLogRepository): OperationLogger

    @Binds
    fun bindExecutionLogReader(executionLogRepository: OperationLogRepository): ExecutionLogReader

    @Binds
    @Deprecated("удалить")
    fun bindExecutionLogCleaner(executionLogRepository: OperationLogRepository): ExecutionLogCleaner
}