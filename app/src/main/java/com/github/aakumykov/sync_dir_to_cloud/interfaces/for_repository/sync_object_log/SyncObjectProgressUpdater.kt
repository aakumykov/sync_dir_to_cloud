package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log

@Deprecated("Переименовать в SyncObjectLogProgressUpdater")
interface SyncObjectProgressUpdater {
    suspend fun updateProgress(objectId: String, taskId: String, executionId: String, progressAsPartOf100: Int)
}