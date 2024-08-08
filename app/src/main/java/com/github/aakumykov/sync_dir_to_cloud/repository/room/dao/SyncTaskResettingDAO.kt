package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SyncTaskResettingDAO {

    @Query("UPDATE sync_tasks SET " +
            "execution_state = 'NEVER', " +
            "execution_error = null, " +
            "source_reading_state = 'NEVER', " +
            "source_reading_error = null, " +
            "last_start = 0, " +
            "last_finish = 0, " +
            "total_objects_count = 0, " +
            "synced_objects_count = 0 " +
            "WHERE id = :taskId")
    suspend fun resetSyncTask(taskId: String)
}