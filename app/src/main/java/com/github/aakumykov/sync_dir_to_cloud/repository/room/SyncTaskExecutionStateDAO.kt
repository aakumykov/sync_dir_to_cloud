package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState

@Dao
abstract class SyncTaskExecutionStateDAO : ExecutionStateChanger() {

    @Query("UPDATE sync_tasks SET execution_state = :state WHERE id = :taskId")
    abstract override suspend fun setStateNotUseDirectly(taskId: String, state: ExecutionState)

    @Query("UPDATE sync_tasks SET execution_error = :errorMsg WHERE id = :taskId")
    abstract override suspend fun setErrorMsgNotUseDirectly(taskId: String, errorMsg: String)
}