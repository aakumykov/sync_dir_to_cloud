package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FullSyncTask

@Dao
interface FullSyncTaskDAO {

    @Transaction
    @Query("SELECT * FROM sync_tasks")
    suspend fun getFullSyncTask(id: String): FullSyncTask

    @Transaction
    @Insert
    suspend fun addFullSyncTask(fullSyncTask: FullSyncTask)

    @Transaction
    @Delete
    fun delete(fullSyncTask: FullSyncTask)

    @Transaction
    @Update
    fun update(fullSyncTask: FullSyncTask)

}