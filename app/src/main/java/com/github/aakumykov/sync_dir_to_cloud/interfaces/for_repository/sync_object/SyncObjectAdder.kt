package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

@Deprecated("TODO: переименовать в SyncObjectAdder")
interface SyncObjectAdder {
    suspend fun addSyncObject(syncObject: SyncObject)
}
