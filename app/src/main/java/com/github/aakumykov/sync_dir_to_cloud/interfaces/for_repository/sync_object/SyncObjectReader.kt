package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.ReadingStrategy

interface SyncObjectReader {

    @Deprecated("Пересмотреть использование")
    suspend fun getObjectsNeedsToBeSynced(taskId: String): List<SyncObject>

    suspend fun getSyncObjectListAsLiveData(taskId: String): LiveData<List<SyncObject>>

    suspend fun getSyncObject(taskId: String, name: String): SyncObject?

    suspend fun getAllObjectsForTask(taskId: String): List<SyncObject>

    suspend fun getObjectsForTaskWithModificationState(taskId: String, modificationState: ModificationState): List<SyncObject>

    suspend fun getObjectsForTaskWithSyncState(taskId: String, syncState: ExecutionState): List<SyncObject>

    @Deprecated("Не используется")
    suspend fun getList(taskId: String, readingStrategy: ReadingStrategy): List<SyncObject>

    suspend fun getInTargetMissingObjects(taskId: String): List<SyncObject>
}