package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

interface SyncObjectUpdater {
    suspend fun updateSyncObject(modifiedSyncObject: SyncObject)
    suspend fun setIsExistsInTarget(objectId: String, isExists: Boolean)
    suspend fun markAsUnchanged(objectId: String)
    suspend fun markAsNew(objectId: String)
    suspend fun updateName(objectId: String, newName: String)
}