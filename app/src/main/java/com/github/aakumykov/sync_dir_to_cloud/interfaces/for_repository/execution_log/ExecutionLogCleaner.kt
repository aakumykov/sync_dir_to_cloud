package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log

interface ExecutionLogCleaner {
    @Deprecated("удалить")
    suspend fun clearExecutionLog()
}