package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth

@Dao
interface CloudAuthDAO {

    @Query("SELECT * FROM cloud_auth")
    fun list(): LiveData<List<CloudAuth>>

    @Insert
    suspend fun add(cloudAuth: CloudAuth)

    @Query("SELECT count(id) FROM cloud_auth WHERE name = :name")
    suspend fun hasName(name: String): Boolean

    @Query("SELECT * FROM cloud_auth WHERE id = :id")
    suspend fun get(id: String): CloudAuth

    @Query("SELECT * FROM cloud_auth WHERE id = :authId")
    fun getBlocking(authId: String): CloudAuth

    @Query("SELECT * FROM cloud_auth WHERE id = :authId")
    suspend fun getNullable(authId: String): CloudAuth?
}