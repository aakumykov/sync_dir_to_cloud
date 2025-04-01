package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskReader {
    @Deprecated("Сделать возвращаемые значения nullable")
    suspend fun getSyncTask(id: String): SyncTask

    suspend fun getSyncTaskNullable(id: String): SyncTask?

    @Deprecated("Сделать возвращаемые значения nullable")
    suspend fun getSyncTaskAsLiveData(taskId: String): LiveData<SyncTask>

    @Deprecated("Сделать возвращаемые значения nullable")
    suspend fun listSyncTasks(): LiveData<List<SyncTask>>

    suspend fun getAllTasks(): List<SyncTask>
}