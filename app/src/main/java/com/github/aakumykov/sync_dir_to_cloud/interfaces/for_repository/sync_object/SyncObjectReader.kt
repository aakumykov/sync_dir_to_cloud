package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

interface SyncObjectReader {
    suspend fun getNewAndChangedSyncObjectsForTask(taskId: String): List<SyncObject>
    suspend fun getSyncObjectListAsLiveData(taskId: String): LiveData<List<SyncObject>>
    suspend fun getSyncObject(name: String, relativeParentDirPath: String): SyncObject?

    suspend fun getObjectsForTask(taskId: String, modificationState: ModificationState): List<SyncObject>
}