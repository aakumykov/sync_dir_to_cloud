package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

interface SyncTaskMetadataReader {

    suspend fun getSourceBackupsDirName(taskId: String): String?
    suspend fun getTargetBackupsDirName(taskId: String): String?

    fun getStartingTime(taskId: String): Long?
}