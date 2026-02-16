package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem

@Dao
interface TaskLogger2DAO {

    @Insert
    suspend fun add(taskLogItem: TaskLogItem)


    @Query("SELECT * FROM ${TaskLogItem.TABLE_NAME} " +
            "WHERE ${GlobalConstants.TASK_ID} = :taskId")
    fun getLogsForTask(taskId: String): LiveData<List<TaskLogItem>>
}