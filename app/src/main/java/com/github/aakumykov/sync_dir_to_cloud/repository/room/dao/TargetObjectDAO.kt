package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TargetObject

@Dao
interface TargetObjectDAO {

    @Insert
    suspend fun add(targetObject: TargetObject)
}