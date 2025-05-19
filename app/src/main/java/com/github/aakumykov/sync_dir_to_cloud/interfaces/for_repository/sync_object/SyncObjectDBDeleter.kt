package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

interface SyncObjectDBDeleter {
    suspend fun deleteObjectWithDeletedState(objectId: String)
    suspend fun deleteAllObjectsForTask(taskId: String)
    suspend fun deleteProcessedObjectsWithDeletedState(taskId: String)
    suspend fun deleteObjectWithId(objectId: String)
}