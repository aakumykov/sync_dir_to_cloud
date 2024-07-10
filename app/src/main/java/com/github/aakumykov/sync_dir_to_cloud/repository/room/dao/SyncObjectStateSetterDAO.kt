package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState

@Dao
interface SyncObjectStateSetterDAO {

    @Query("UPDATE sync_objects SET " +
            "target_reading_state = :state, " +
            "sync_error = :errorMsg " +
            "WHERE id = :objectId")
    fun setTargetReadingState(objectId: String, state: ExecutionState, errorMsg: String)

    @Query("UPDATE sync_objects SET " +
            "backup_state = :state, " +
            "sync_error = :errorMsg " +
            "WHERE id = :objectId")
    fun setBackupState(objectId: String, state: ExecutionState, errorMsg: String)


    @Query("UPDATE sync_objects SET " +
            "deletion_state = :state, " +
            "sync_error = :errorMsg " +
            "WHERE id = :objectId")
    fun setDeletionState(objectId: String, state: ExecutionState, errorMsg: String)


    @Query("UPDATE sync_objects SET " +
            "restoration_state = :state, " +
            "sync_error = :errorMsg " +
            "WHERE id = :objectId")
    fun setRestorationState(objectId: String, state: ExecutionState, errorMsg: String)


    @Query("UPDATE sync_objects SET " +
            "sync_state = :state, " +
            "sync_error = :errorMsg " +
            "WHERE id = :objectId")
    fun setSyncState(objectId: String, state: ExecutionState, errorMsg: String)
}