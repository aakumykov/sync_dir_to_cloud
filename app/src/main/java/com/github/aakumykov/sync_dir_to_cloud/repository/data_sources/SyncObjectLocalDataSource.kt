package com.github.aakumykov.sync_dir_to_cloud.repository.data_sources

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
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

    suspend fun getSyncObjectsForTask(taskId: String): List<SyncObject> {
        return withContext(Dispatchers.IO) {
            syncObjectDAO.getSyncObjectsForTask(taskId)
        }
    }

    suspend fun setState(syncObjectId: String, state: SyncObject.State) {
        return withContext(Dispatchers.IO) {
            syncObjectDAO.setState(syncObjectId, state)
        }
    }

    suspend fun setErrorMsg(syncObjectId: String, errorMsg: String) {
        return withContext(Dispatchers.IO) {
            syncObjectDAO.setErrorMsg(syncObjectId, errorMsg)
        }
    }

    suspend fun getSyncObjectList(taskId: String): LiveData<List<SyncObject>> {
        return withContext(Dispatchers.IO) {
            syncObjectDAO.getSyncObjectList(taskId)
        }
    }

    suspend fun deleteSyncObjectOfTask(taskId: String) {
        return withContext(Dispatchers.IO) {
            syncObjectDAO.deleteObjectsForTask(taskId)
        }
    }
}