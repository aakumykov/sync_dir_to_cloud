package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SyncObjectBadStateResettingDAO {

    @Query("UPDATE sync_objects " +
            "SET target_reading_state = 'NEVER' " +
            "WHERE target_reading_state IN ('ERROR','RUNNING') " +
            "AND task_id = :taskId")
    fun resetTargetReadingBadState(taskId: String)

    @Query("UPDATE sync_objects " +
            "SET backup_state = 'NEVER' " +
            "WHERE target_reading_state IN ('ERROR','RUNNING') " +
            "AND task_id = :taskId")
    fun resetBackupBadState(taskId: String)

    @Query("UPDATE sync_objects " +
            "SET deletion_state = 'NEVER' " +
            "WHERE target_reading_state IN ('ERROR','RUNNING') " +
            "AND task_id = :taskId")
    fun resetDeletionBadState(taskId: String)

    @Query("UPDATE sync_objects " +
            "SET restoration_state = 'NEVER' " +
            "WHERE target_reading_state IN ('ERROR','RUNNING') " +
            "AND task_id = :taskId")
    fun resetRestorationBadState(taskId: String)
}