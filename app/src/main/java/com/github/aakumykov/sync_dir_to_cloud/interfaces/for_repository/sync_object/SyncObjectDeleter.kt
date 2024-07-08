package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

interface SyncObjectDeleter {
    @Deprecated("Нужно удалять по одному")
    suspend fun clearObjectsWasSuccessfullyDeleted(taskId: String)
    suspend fun deleteObjectWithDeletedState(objectId: String)
}