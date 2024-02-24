package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

interface SyncObjectDeleter {
    suspend fun clearObjectsWasSuccessfullyDeleted(taskId: String)
}