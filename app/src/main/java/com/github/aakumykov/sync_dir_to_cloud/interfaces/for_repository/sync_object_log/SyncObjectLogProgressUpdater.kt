package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log

interface SyncObjectLogProgressUpdater {
    suspend fun updateProgress(objectId: String, taskId: String, executionId: String, progressAsPartOf100: Int)
}