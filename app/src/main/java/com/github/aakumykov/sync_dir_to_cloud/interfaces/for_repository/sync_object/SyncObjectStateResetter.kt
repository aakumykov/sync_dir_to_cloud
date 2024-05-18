package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageHalf

interface SyncObjectStateResetter {
    // TODO: разделить на два интерфейса
    suspend fun markAllObjectsAsDeleted(storageHalf: StorageHalf, taskId: String)
    suspend fun markBadStatesAsNeverSynced(taskId: String)
}