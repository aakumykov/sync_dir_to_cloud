package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

@Dao
interface SyncTaskDAO {

    @Query("SELECT * FROM sync_tasks")
    fun list(): LiveData<List<SyncTask>>

    @Insert
    fun add(syncTask: SyncTask)

    @Query("SELECT * FROM sync_tasks WHERE id = :id")
    fun get(id: String): SyncTask

    @Delete
    fun delete(syncTask: SyncTask)

    @Update
    fun update(syncTask: SyncTask)
}