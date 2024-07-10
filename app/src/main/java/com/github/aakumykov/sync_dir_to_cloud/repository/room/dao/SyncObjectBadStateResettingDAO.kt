package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SyncObjectBadStateResettingDAO {

    @Query("UPDATE sync_objects " +
            "SET target_reading_state = 'NEVER' " +
            "WHERE target_reading_state = 'ERROR' " +
            "AND task_id = :taskId")
    fun resetTargetReadingErrorState(taskId: String)

    @Query("UPDATE sync_objects " +
            "SET backup_state = 'NEVER' " +
            "WHERE target_reading_state = 'ERROR' " +
            "AND task_id = :taskId")
    fun resetBackupErrorState(taskId: String)

    @Query("UPDATE sync_objects " +
            "SET deletion_state = 'NEVER' " +
            "WHERE target_reading_state = 'ERROR' " +
            "AND task_id = :taskId")
    fun resetDeletionErrorState(taskId: String)

    @Query("UPDATE sync_objects " +
            "SET restoration_state = 'NEVER' " +
            "WHERE target_reading_state = 'ERROR' " +
            "AND task_id = :taskId")
    fun resetRestorationErrorState(taskId: String)
}