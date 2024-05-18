package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageHalf

interface SyncObjectReader {

    suspend fun getObjectsNeedsToBeSynced(storageHalf: StorageHalf, taskId: String): List<SyncObject>

    suspend fun getSyncObjectListAsLiveData(storageHalf: StorageHalf, taskId: String): LiveData<List<SyncObject>>

    suspend fun getSyncObject(storageHalf: StorageHalf, taskId: String, name: String): SyncObject?

    suspend fun getObjectsForTask(taskId: String, modificationState: ModificationState): List<SyncObject>
}