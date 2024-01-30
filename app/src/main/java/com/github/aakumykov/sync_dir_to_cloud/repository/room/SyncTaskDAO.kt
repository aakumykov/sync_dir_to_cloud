package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

@Dao
interface SyncTaskDAO {

    @Query("SELECT * FROM sync_tasks")
    fun list(): LiveData<List<SyncTask>>

    @Insert
    suspend fun add(syncTask: SyncTask)

    @Query("SELECT * FROM sync_tasks WHERE id = :id")
    suspend fun get(id: String): SyncTask

    @Query("SELECT * FROM sync_tasks WHERE id = :taskId")
    suspend fun getAsLiveData(taskId: String): LiveData<SyncTask>

    @Delete
    suspend fun delete(syncTask: SyncTask)

    @Query("DELETE FROM sync_tasks WHERE id = :taskId")
    suspend fun delete(taskId: String)

    @Update
    suspend fun update(syncTask: SyncTask)
}