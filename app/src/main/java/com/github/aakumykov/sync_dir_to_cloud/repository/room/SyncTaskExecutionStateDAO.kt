package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

@Dao
abstract class SyncTaskExecutionStateDAO : SimpleStateChanger() {

    @Query("UPDATE sync_tasks SET execution_state = :state WHERE id = :taskId")
    abstract override suspend fun setStateNotUseDirectly(taskId: String, state: SyncTask.SimpleState)

    @Query("UPDATE sync_tasks SET execution_error = :errorMsg WHERE id = :taskId")
    abstract override suspend fun setErrorMsgNotUseDirectly(taskId: String, errorMsg: String)
}