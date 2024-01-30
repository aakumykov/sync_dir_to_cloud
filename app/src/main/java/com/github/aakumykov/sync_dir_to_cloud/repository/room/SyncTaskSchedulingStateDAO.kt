package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

@Dao
abstract class SyncTaskSchedulingStateDAO {

    @Transaction @Update
    suspend fun setIdleState(taskId: String) {
        setErrorMsgNotUseDirectly(taskId, "")
        setStateNotUseDirectly(taskId, SyncTask.SimpleState.IDLE)
    }

    @Transaction @Update
    suspend fun setBusyState(taskId: String) {
        setErrorMsgNotUseDirectly(taskId, "")
        setStateNotUseDirectly(taskId, SyncTask.SimpleState.BUSY)
    }

    @Transaction @Update
    suspend fun setErrorState(taskId: String, errorMsg: String) {
        setErrorMsgNotUseDirectly(taskId, errorMsg)
        setStateNotUseDirectly(taskId, SyncTask.SimpleState.ERROR)
    }


    @Query("UPDATE sync_tasks SET scheduling_error = :errorMsg WHERE id = :taskId")
    protected abstract suspend fun setErrorMsgNotUseDirectly(taskId: String, errorMsg: String)

    @Query("UPDATE sync_tasks SET scheduling_state = :state WHERE id = :taskId")
    protected abstract suspend fun setStateNotUseDirectly(taskId: String, state: SyncTask.SimpleState)
}