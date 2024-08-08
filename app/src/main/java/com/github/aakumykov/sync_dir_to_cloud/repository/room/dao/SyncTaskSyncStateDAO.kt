package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState

@Dao
abstract class SyncTaskSyncStateDAO : SyncStateChanger() {

    @Query("UPDATE sync_tasks SET execution_error = :errorMsg WHERE id = :taskId")
    abstract override suspend fun setErrorMsgNotUseDirectly(taskId: String, errorMsg: String)

    @Query("UPDATE sync_tasks SET execution_state = :state WHERE id = :taskId")
    abstract override suspend fun setStateNotUseDirectly(taskId: String, state: ExecutionState)
}