package com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage

@Dao
interface TestSyncObjectDAO {

    @Query("UPDATE sync_objects SET state_in_storage = :stateInStorage WHERE name = :fileName")
    fun updateStateInStorageForFileName(fileName: String, stateInStorage: StateInStorage)

}