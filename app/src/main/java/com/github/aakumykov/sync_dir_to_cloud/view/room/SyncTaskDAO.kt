package com.github.aakumykov.sync_dir_to_cloud.view.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.view.domain.entities.SyncTask


@Dao
interface SyncTaskDAO {

    @Query("SELECT * FROM sync_tasks")
    fun list(): LiveData<List<SyncTask?>?>?

    @Insert
    fun add(syncTask: SyncTask?)
}