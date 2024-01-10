package com.github.aakumykov.sync_dir_to_cloud.repository.data_sources

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskStateDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


// FIXME: я же уже использую диспетчер в репозитории (!)

class SyncTaskLocalDataSource @Inject constructor(
    private val syncTaskDAO: SyncTaskDAO,
    private val syncTaskStateDAO: SyncTaskStateDAO
) {
    suspend fun listSyncTasks(): LiveData<List<SyncTask>> {
        return withContext(Dispatchers.IO) {
            syncTaskDAO.list()
        }
    }

    suspend fun addTask(syncTask: SyncTask) {
        return withContext(Dispatchers.IO) {
            syncTaskDAO.add(syncTask)
        }
    }

    suspend fun getTask(id: String): SyncTask {
        return withContext(Dispatchers.IO) {
            syncTaskDAO.get(id)
        }
    }

    suspend fun delete(syncTask: SyncTask) {
        return withContext(Dispatchers.IO) {
            syncTaskDAO.delete(syncTask)
        }
    }

    suspend fun update(syncTask: SyncTask) {
        return withContext(Dispatchers.IO) {
            syncTaskDAO.update(syncTask)
        }
    }

    suspend fun setState(taskId: String, state: SyncTask.State) {
        syncTaskStateDAO.setState(taskId, state)
    }

    suspend fun getSyncTaskState(taskId: String): Flow<SyncTask.State> {
        return syncTaskStateDAO.getSyncTaskStateFlow(taskId)
    }
}