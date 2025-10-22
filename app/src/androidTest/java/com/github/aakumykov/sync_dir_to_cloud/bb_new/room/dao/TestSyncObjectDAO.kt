package com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide

@Dao
interface TestSyncObjectDAO {

    @Query("UPDATE sync_objects " +
            "SET state_in_storage = :stateInStorage " +
            "WHERE name = :fileName " +
            "AND sync_side = :syncSide")
    fun updateStateInStorageForFileName(
        fileName: String,
        syncSide: SyncSide,
        stateInStorage: StateInStorage
    )

    @Query("SELECT state_in_storage FROM sync_objects WHERE " +
            "name = :fileName " +
            "AND sync_side = :syncSide")
    fun getStateInStorage(fileName: String, syncSide: SyncSide): StateInStorage


    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId AND sync_side = :syncSide")
    fun list(taskId: String, syncSide: SyncSide): List<SyncObject>
}