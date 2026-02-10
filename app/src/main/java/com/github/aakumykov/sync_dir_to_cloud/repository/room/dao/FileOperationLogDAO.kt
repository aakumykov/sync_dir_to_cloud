package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.FileOperationLogItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FileOperationLogDAO {

    @Insert
    suspend fun add(item: FileOperationLogItem)

    @Update
    suspend fun update(item: FileOperationLogItem)
}