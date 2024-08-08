package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState

@Dao
abstract class BadObjectStateResettingDAO {

    @Transaction @Update
    suspend fun markRunningStateAsNeverSynced(taskId: String) {
        changeSyncState(taskId, ExecutionState.RUNNING, ExecutionState.NEVER)
    }

    @Transaction @Update
    suspend fun markErrorStateAsNeverSynced(taskId: String) {
        changeSyncState(taskId, ExecutionState.ERROR, ExecutionState.NEVER)
    }


    @Query("UPDATE sync_objects SET sync_state = :syncState " +
            "WHERE (sync_state = :sourceSyncState AND task_id = :taskId)")
    abstract suspend fun changeSyncState(taskId: String, sourceSyncState: ExecutionState, syncState: ExecutionState)
}