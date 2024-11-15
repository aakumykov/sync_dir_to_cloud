package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem

@Dao
interface ExecutionLogDAO {

    @Insert
    suspend fun addItem(executionLogItem: ExecutionLogItem)
}