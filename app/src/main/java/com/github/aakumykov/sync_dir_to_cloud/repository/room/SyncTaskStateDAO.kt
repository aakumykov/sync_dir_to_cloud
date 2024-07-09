package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

@Dao
interface SyncTaskStateDAO {

    @Deprecated("Используй suspend-вариант")
    @Query("UPDATE sync_tasks SET state = :state WHERE id = :taskId")
    fun setState(taskId: String, state: SyncTask.State)

    @Query("UPDATE sync_tasks SET state = :state WHERE id = :taskId")
    suspend fun setStateSuspend(taskId: String, state: SyncTask.State)

    @Query("UPDATE sync_tasks SET is_enabled = :isEnabled WHERE id = :taskId")
    suspend fun setEnabled(taskId: String, isEnabled: Boolean)

    @Query("SELECT state FROM sync_tasks WHERE id = :taskId")
    suspend fun getState(taskId: String): SyncTask.State

    @Query("UPDATE sync_tasks SET execution_state = :syncState WHERE id = :taskId")
    suspend fun setSyncState(taskId: String, syncState: ExecutionState)


    @Query("UPDATE sync_tasks SET " +
            "source_reading_state = :state, " +
            "source_reading_error = :errorMsg " +
            "WHERE id = :taskId")
    fun setSourceReadingState(taskId: String, state: ExecutionState, errorMsg: String?)
}