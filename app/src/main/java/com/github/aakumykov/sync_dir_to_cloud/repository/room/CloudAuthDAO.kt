package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth

@Dao
interface CloudAuthDAO {

    @Insert
    fun add(cloudAuth: CloudAuth)

    @Query("SELECT * FROM cloud_auth")
    fun list(): LiveData<List<CloudAuth>>
}