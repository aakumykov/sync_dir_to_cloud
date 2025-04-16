package com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth

@Dao
interface TestCloudAuthDAO {

    @Insert
    fun add(cloudAuth: CloudAuth)

    @Query("SELECT * FROM cloud_auth WHERE id = :authId")
    fun get(authId: String): CloudAuth?

    @Query("SELECT * FROM cloud_auth")
    fun getAll(): List<CloudAuth>

    @Query("DELETE FROM cloud_auth WHERE id = :authId")
    fun delete(authId: String)

    @Query("DELETE FROM cloud_auth")
    fun deleteAll()

    @Query("SELECT count(id) FROM cloud_auth")
    fun count(): Int
}