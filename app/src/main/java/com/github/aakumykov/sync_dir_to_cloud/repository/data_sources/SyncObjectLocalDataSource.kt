package com.github.aakumykov.sync_dir_to_cloud.repository.data_sources

import com.github.aakumykov.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncObjectDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncObjectLocalDataSource @Inject constructor(private val syncObjectDAO: SyncObjectDAO) {

    suspend fun addSyncObject(syncObject: SyncObject) {
        return withContext(Dispatchers.IO) {
            syncObjectDAO.add(syncObject)
        }
    }
}