package com.github.aakumykov.sync_dir_to_cloud.repository.data_sources

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncTaskLocalDataSource @Inject constructor(private val mSyncTaskDAO: SyncTaskDAO) {

    suspend fun listSyncTasks(): LiveData<List<SyncTask>> {
        return withContext(Dispatchers.IO) {
            mSyncTaskDAO.list()
        }
    }

    suspend fun addTask(syncTask: SyncTask) {
        return withContext(Dispatchers.IO) {
            mSyncTaskDAO.add(syncTask)
        }
    }

    suspend fun getTask(id: String): SyncTask {
        return withContext(Dispatchers.IO) {
            mSyncTaskDAO.get(id)
        }
    }

    suspend fun delete(taskId: String) {
        return withContext(Dispatchers.IO) {
            mSyncTaskDAO.delete(taskId)
        }
    }

    suspend fun update(syncTask: SyncTask) {
        return withContext(Dispatchers.IO) {
            mSyncTaskDAO.update(syncTask)
        }
    }
}