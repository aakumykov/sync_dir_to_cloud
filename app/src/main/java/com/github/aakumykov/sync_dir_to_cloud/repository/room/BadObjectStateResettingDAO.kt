package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncState

@Dao
abstract class BadObjectStateResettingDAO {

    @Transaction @Update
    suspend fun markRunningStateAsNeverSynced(taskId: String) {
        changeSyncState(taskId, SyncState.RUNNING, SyncState.NEVER)
    }

    @Transaction @Update
    suspend fun markErrorStateAsNeverSynced(taskId: String) {
        changeSyncState(taskId, SyncState.ERROR, SyncState.NEVER)
    }


    @Query("UPDATE sync_objects SET sync_state = :syncState " +
            "WHERE (sync_state = :sourceSyncState AND task_id = :taskId)")
    abstract suspend fun changeSyncState(taskId: String, sourceSyncState: SyncState, syncState: SyncState)
}