package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

interface SyncObjectReader {
    suspend fun getSyncObjectsForTask(taskId: String): List<SyncObject>
    suspend fun getSyncObjectListAsLiveData(taskId: String): LiveData<List<SyncObject>>
}