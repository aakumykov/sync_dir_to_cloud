package com.github.aakumykov.sync_dir_to_cloud.repository.data_sources

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FullSyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.FullSyncTaskDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FullSyncTaskLocalDataSource @Inject constructor(
    private val fullSyncTaskDAO: FullSyncTaskDAO
) {
    suspend fun getFullSyncTask(taskId: String): FullSyncTask {
        return withContext(Dispatchers.IO) {
            fullSyncTaskDAO.getFullSyncTask(taskId)
        }
    }

    suspend fun addFullSyncTask(fullSyncTask: FullSyncTask) {
        withContext(Dispatchers.IO) {
            fullSyncTaskDAO.addFullSyncTask(fullSyncTask)
        }
    }

    suspend fun deleteFullSyncTask(fullSyncTask: FullSyncTask) {
        withContext(Dispatchers.IO) {
            fullSyncTaskDAO.delete(fullSyncTask)
        }
    }

    suspend fun update(fullSyncTask: FullSyncTask) {
        withContext(Dispatchers.IO) {
            fullSyncTaskDAO.update(fullSyncTask)
        }
    }
}