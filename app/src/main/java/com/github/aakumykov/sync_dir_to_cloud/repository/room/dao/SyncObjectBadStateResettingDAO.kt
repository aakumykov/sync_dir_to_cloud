package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SyncObjectBadStateResettingDAO {


    @Query("UPDATE sync_objects " +
            "SET target_reading_state = "+RESTORED_STATE+" " +
            "WHERE target_reading_state IN ("+ BAD_STATE_LIST+") " +
            "AND task_id = :taskId")
    fun resetTargetReadingBadState(taskId: String)


    @Query("UPDATE sync_objects " +
            "SET backup_state = "+RESTORED_STATE+" " +
            "WHERE target_reading_state IN ("+ BAD_STATE_LIST+") " +
            "AND task_id = :taskId")
    fun resetBackupBadState(taskId: String)


    @Query("UPDATE sync_objects " +
            "SET deletion_state = "+RESTORED_STATE+" " +
            "WHERE target_reading_state IN ("+ BAD_STATE_LIST+") " +
            "AND task_id = :taskId")
    fun resetDeletionBadState(taskId: String)


    @Query("UPDATE sync_objects " +
            "SET restoration_state = "+RESTORED_STATE+" " +
            "WHERE target_reading_state IN ("+ BAD_STATE_LIST+") " +
            "AND task_id = :taskId")
    fun resetRestorationBadState(taskId: String)


    @Query("UPDATE sync_objects " +
            "SET sync_state = "+RESTORED_STATE+" " +
            "WHERE sync_state IN ("+BAD_STATE_LIST+") " +
            "AND task_id = :taskId")
    fun resetSyncBadState(taskId: String)


    companion object {
        const val BAD_STATE_LIST = "'ERROR','RUNNING'"
        const val RESTORED_STATE = "'NEVER'"
    }
}