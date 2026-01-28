package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem

@Dao
interface TaskLogger2DAO {

    @Insert
    suspend fun add(taskLogItem: TaskLogItem)


    /*@Query("SELECT * FROM ${TaskLogItem.TABLE_NAME} " +
            "WHERE ${GlobalConstants.FIELD_TASK_ID} = :taskId " +
            "AND ${GlobalConstants.FIELD_EXECUTION_ID} = :executionId " +
            "ORDER BY ${GlobalConstants.FIELD_TIMESTAMP}")
    suspend fun list(taskId: String, executionId: String): List<TaskLogItem>*/


    /*@Query("DELETE FROM ${TaskLogItem.TABLE_NAME} " +
            "WHERE ${GlobalConstants.FIELD_TASK_ID} = :taskId")
    suspend fun deleteAllForTask(taskId: String)*/
}