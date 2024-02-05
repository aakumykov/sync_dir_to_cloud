package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

@Dao
interface SyncObjectDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(syncObject: SyncObject)

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId")
    fun getSyncObjectsForTask(taskId: String): List<SyncObject>

    @Query("UPDATE sync_objects SET execution_state = :state WHERE id = :syncObjectId")
    fun setExecutionState(syncObjectId: String, state: SyncObject.State)

    @Query("UPDATE sync_objects SET error_msg = :errorMsg WHERE id = :syncObjectId")
    fun setErrorMsg(syncObjectId: String, errorMsg: String)

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId")
    fun getSyncObjectList(taskId: String): LiveData<List<SyncObject>>

    @Query("DELETE FROM sync_objects WHERE task_id = :taskId")
    fun deleteObjectsForTask(taskId: String)
}