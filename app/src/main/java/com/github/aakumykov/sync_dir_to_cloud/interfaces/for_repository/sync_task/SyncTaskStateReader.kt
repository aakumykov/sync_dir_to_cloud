package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import kotlinx.coroutines.flow.Flow

interface SyncTaskStateReader {
    suspend fun getSyncTaskStateAsLiveData(taskId: String): LiveData<SyncTask.State>
    suspend fun getSyncTaskStateAsFlow(taskId: String): Flow<SyncTask.State>
}