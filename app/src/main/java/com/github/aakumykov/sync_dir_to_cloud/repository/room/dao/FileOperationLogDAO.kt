package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.FileOperationLogItem2

@Dao
interface FileOperationLogDAO {

    @Insert
    suspend fun add(taskLogItem: FileOperationLogItem2)


    @Query("SELECT * FROM ${FileOperationLogItem2.TABLE_NAME} " +
            "WHERE ${GlobalConstants.FIELD_TASK_ID} = :taskId " +
            "AND ${GlobalConstants.FIELD_EXECUTION_ID} = :executionId " +
            "ORDER BY ${GlobalConstants.FIELD_TIMESTAMP}")
    suspend fun list(taskId: String, executionId: String): List<FileOperationLogItem2>


    @Query("DELETE FROM ${FileOperationLogItem2.TABLE_NAME} " +
            "WHERE ${GlobalConstants.FIELD_TASK_ID} = :taskId")
    suspend fun deleteAllForTask(taskId: String)
}