package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

interface SyncObjectStateResetter {
    // TODO: разделить на два интерфейса
    suspend fun markAllObjectsAsDeleted(taskId: String)
    suspend fun markBadStatesAsNeverSynced(taskId: String)
}