package com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

@Dao
interface TestSyncTaskDAO {

    @Insert
    fun add(syncTask: SyncTask)

    @Query("SELECT * FROM sync_tasks WHERE id = :taskId")
    fun get(taskId: String): SyncTask?


    @Query("SELECT count(id) FROM sync_tasks")
    fun count(): Int


    @Query("DELETE FROM sync_tasks WHERE id = :taskId")
    fun delete(taskId: String)


    @Query("DELETE FROM sync_tasks")
    fun deleteAll()
}