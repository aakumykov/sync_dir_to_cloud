package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log

interface SyncObjectLogDeleter {
    suspend fun deleteLogsForTask(taskId: String)
}