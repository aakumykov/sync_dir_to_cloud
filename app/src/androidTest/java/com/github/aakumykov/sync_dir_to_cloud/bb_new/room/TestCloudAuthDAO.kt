package com.github.aakumykov.sync_dir_to_cloud.bb_new.room

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

}