package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.BasicLogItem

@Dao
interface TaskLogger2DAO {

    @Insert
    suspend fun add(taskLogItem: TaskLogItem)

    @Update
    suspend fun update(taskLogItem: TaskLogItem)

    @Query("SELECT * FROM ${TaskLogItem.TABLE_NAME} " +
            "WHERE ${GlobalConstants.TASK_ID} = :taskId " +
            "ORDER BY ${BasicLogItem.FIELD_FINISH_TIME} DESC")
    fun listAsLiveData(taskId: String): LiveData<List<TaskLogItem>>
}