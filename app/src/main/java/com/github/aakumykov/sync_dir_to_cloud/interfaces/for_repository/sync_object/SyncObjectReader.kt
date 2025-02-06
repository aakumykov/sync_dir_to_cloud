package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.Side
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.ReadingStrategy

interface SyncObjectReader {

    @Deprecated("Пересмотреть использование")
    suspend fun getObjectsNeedsToBeSynced(taskId: String): List<SyncObject>

    suspend fun getSyncObjectListAsLiveData(taskId: String): LiveData<List<SyncObject>>

    suspend fun getSyncObject(
        taskId: String,
        side: Side,
        name: String,
        relativeParentDirPath: String
    ): SyncObject?

    suspend fun getAllObjectsForTask(taskId: String): List<SyncObject>

    suspend fun getAllObjectsForTask(side: Side, taskId: String, ): List<SyncObject>

    @Deprecated("Сделать отдельные методы для файлов и каталогов")
    suspend fun getObjectsForTaskWithModificationState(taskId: String, stateInStorage: StateInStorage): List<SyncObject>

    @Deprecated("Сделать отдельные методы для файлов и каталогов")
    suspend fun getObjectsForTaskWithSyncState(taskId: String, syncState: ExecutionState): List<SyncObject>

    @Deprecated("Не используется")
    suspend fun getList(taskId: String, readingStrategy: ReadingStrategy): List<SyncObject>

    suspend fun getInTargetMissingObjects(taskId: String): List<SyncObject>
}