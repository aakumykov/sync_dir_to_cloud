package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

@Dao
interface SyncTaskDAO {

    @Query("SELECT * FROM sync_tasks")
    fun list(): LiveData<List<SyncTask>>

    @Insert
    fun add(syncTask: SyncTask)

    @Query("SELECT * FROM sync_tasks WHERE id = :taskId")
    fun get(taskId: String): SyncTask

    @Query("DELETE FROM sync_tasks WHERE id = :taskId")
    fun delete(taskId: String)

    @Update
    fun update(syncTask: SyncTask)
}