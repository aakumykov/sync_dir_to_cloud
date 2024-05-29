package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageHalf
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.ReadingStrategy

interface SyncObjectReader {

    suspend fun getObjectsNeedsToBeSynced(storageHalf: StorageHalf, taskId: String): List<SyncObject>

    suspend fun getSyncObjectListAsLiveData(storageHalf: StorageHalf, taskId: String): LiveData<List<SyncObject>>

    suspend fun getSyncObject(storageHalf: StorageHalf, taskId: String, name: String): SyncObject?

    suspend fun getObjectsForTask(storageHalf: StorageHalf, taskId: String, modificationState: ModificationState): List<SyncObject>

    suspend fun getObjectsForTask(storageHalf: StorageHalf, taskId: String, syncState: ExecutionState): List<SyncObject>

    @Deprecated("Не используется")
    suspend fun getList(taskId: String, storageHalf: StorageHalf, readingStrategy: ReadingStrategy): List<SyncObject>

    suspend fun getInTargetMissingObjects(taskId: String): List<SyncObject>
}