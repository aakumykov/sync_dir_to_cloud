package com.github.aakumykov.sync_dir_to_cloud.repository.data_sources

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskSchedulingStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskStateDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


// FIXME: я же уже использую диспетчер в репозитории (!)

class SyncTaskLocalDataSource @Inject constructor(
    private val syncTaskDAO: SyncTaskDAO,
    private val syncTaskStateDAO: SyncTaskStateDAO,
    private val syncTaskSchedulingStateDAO: SyncTaskSchedulingStateDAO
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

    suspend fun delete(taskId: String) {
        return withContext(Dispatchers.IO) {
            syncTaskDAO.delete(taskId)
        }
    }

    suspend fun update(syncTask: SyncTask) {
        return withContext(Dispatchers.IO) {
            syncTaskDAO.update(syncTask)
        }
    }

    suspend fun setState(taskId: String, state: SyncTask.State) {
        return withContext(Dispatchers.IO) {
            syncTaskStateDAO.setState(taskId, state)
        }
    }

    suspend fun getTaskAsLiveData(taskId: String): LiveData<SyncTask> {
        return withContext(Dispatchers.IO) {
            syncTaskDAO.getAsLiveData(taskId)
        }
    }

    suspend fun setSyncTaskSchedulingState(taskId: String, newSate: SyncTask.SimpleState, errorMsg: String) {
        return withContext(Dispatchers.IO) {
            when(newSate) {
                SyncTask.SimpleState.IDLE -> syncTaskSchedulingStateDAO.setIdleState(taskId)
                SyncTask.SimpleState.BUSY -> syncTaskSchedulingStateDAO.setBusyState(taskId)
                SyncTask.SimpleState.ERROR -> syncTaskSchedulingStateDAO.setErrorState(taskId, errorMsg)
            }
        }
    }
}