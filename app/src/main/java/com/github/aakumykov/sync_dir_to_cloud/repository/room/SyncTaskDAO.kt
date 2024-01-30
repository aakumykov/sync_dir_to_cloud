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

    @Insert
    suspend fun addSuspend(syncTask: SyncTask)

    @Query("SELECT * FROM sync_tasks WHERE id = :id")
    fun get(id: String): SyncTask

    @Query("SELECT * FROM sync_tasks WHERE id = :taskId")
    fun getAsLiveData(taskId: String): LiveData<SyncTask>

    @Delete
    fun delete(syncTask: SyncTask)

    @Query("DELETE FROM sync_tasks WHERE id = :taskId")
    fun delete(taskId: String)

    @Update
    fun update(syncTask: SyncTask)

    @Deprecated("Чтобы задействовать, нужно разобраться с ошибками в suspend-функциях")
    @Update
    suspend fun updateSuspend(syncTask: SyncTask)
}