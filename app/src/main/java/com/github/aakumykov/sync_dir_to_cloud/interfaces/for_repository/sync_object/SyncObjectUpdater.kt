package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

interface SyncObjectUpdater {

    suspend fun updateSyncObject(modifiedSyncObject: SyncObject)

    suspend fun setIsExistsInTarget(objectId: String, isExists: Boolean)

    suspend fun markAsUnchanged(objectId: String)

    @Deprecated("НЕ ИСПОЛЬЗОВАТЬ!")
    suspend fun markAsNew(objectId: String)

    suspend fun markAsDeleted(objectId: String)

    suspend fun updateAsModified(objectId: String, newSize: Long, newMTime: Long)

    suspend fun markJustChecked(objectId: String)

    suspend fun updateName(objectId: String, newName: String)


    suspend fun updateStateInStorage(objectId: String, stateInStorage: StateInStorage)

    suspend fun updateMetadata(objectId: String, size: Long, mTime: Long)
}