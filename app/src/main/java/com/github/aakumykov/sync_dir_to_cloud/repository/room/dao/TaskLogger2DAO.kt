package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem

@Dao
interface TaskLogger2DAO {

    @Insert
    suspend fun add(taskLogItem: TaskLogItem)

    @Update
    suspend fun update(taskLogItem: TaskLogItem)
}