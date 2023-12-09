package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.entities.CloudAuth

@Dao
interface CloudAuthDAO {

    @Insert
    fun add(cloudAuth: CloudAuth)

    @Query("SELECT * FROM cloud_auth")
    fun list(): LiveData<List<CloudAuth>>

    @Query("SELECT * FROM cloud_auth WHERE name = :name")
    fun hasName(name: String): Boolean

    @Query("SELECT * FROM cloud_auth WHERE id = :id")
    fun get(id: String): CloudAuth
}