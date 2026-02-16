package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.view.task_details.model.TaskDetailsItem

@Dao
interface TaskDetailsDAO {

    @Query("SELECT * FROM ${TaskDetailsItem.TABLE_NAME} " +
            "WHERE ${GlobalConstants.TASK_ID} = :taskId " +
            "AND ${GlobalConstants.EXECUTION_ID} = :executionId")
    fun getLogsForTask(taskId: String, executionId: String): LiveData<List<TaskDetailsItem>>
}