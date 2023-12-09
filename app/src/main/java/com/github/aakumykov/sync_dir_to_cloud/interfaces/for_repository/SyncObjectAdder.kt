package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository

import com.github.aakumykov.entities.SyncObject

interface SyncObjectAdder {
    suspend fun addSyncObject(syncObject: SyncObject)
}
