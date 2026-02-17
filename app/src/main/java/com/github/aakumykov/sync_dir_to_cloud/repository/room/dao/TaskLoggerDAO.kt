package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.BasicLogItem

@Dao
interface TaskLoggerDAO {

    @Insert
    suspend fun add(taskLogItem: TaskLogItem)


    @Query(
        "UPDATE ${TaskLogItem.TABLE_NAME} " +
                "SET ${BasicLogItem.FIELD_LOG_ITEM_TYPE} = :logItemType, " +
                "${BasicLogItem.FIELD_SUB_TEXT} = :subText, " +
                "${BasicLogItem.FIELD_FINISH_TIME} = :finishTime " +
                "WHERE ${GlobalConstants.FIELD_ID} = :id"
    )
    suspend fun update(
        id: String,
        logItemType: LogItemType,
        subText: String?,
        finishTime: Long
    )



    @Query("SELECT * FROM ${TaskLogItem.TABLE_NAME} " +
            "WHERE ${GlobalConstants.TASK_ID} = :taskId " +
            "ORDER BY ${BasicLogItem.FIELD_FINISH_TIME} DESC")
    fun listAsLiveData(taskId: String): LiveData<List<TaskLogItem>>
}