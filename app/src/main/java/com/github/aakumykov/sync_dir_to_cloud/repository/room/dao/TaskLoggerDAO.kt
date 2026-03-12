package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.DbFieldNames
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.BasicLogItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskLoggerDAO {

    @Insert
    suspend fun add(taskLogItem: TaskLogItem)


    @Query(
        "UPDATE ${TaskLogItem.TABLE_NAME} " +
                "SET ${BasicLogItem.FIELD_LOG_ITEM_TYPE} = :logItemType, " +
                "${BasicLogItem.FIELD_SUB_TEXT} = :subText, " +
                "${BasicLogItem.FIELD_FINISH_TIME} = :finishTime " +
                "WHERE ${DbFieldNames.FIELD_ID} = :id"
    )
    suspend fun update(
        id: String,
        logItemType: LogItemType,
        subText: String?,
        finishTime: Long
    )


    @Query(SIMPLE_LIST_QUERY)
    fun listAsLiveData(taskId: String): LiveData<List<TaskLogItem>>


    @Query(SIMPLE_LIST_QUERY)
    fun listAsFlow(taskId: String): Flow<List<TaskLogItem>>


    @Query(SIMPLE_LIST_QUERY)
    fun list(taskId: String): List<TaskLogItem>

    companion object {
        const val SIMPLE_LIST_QUERY = "SELECT * FROM ${TaskLogItem.TABLE_NAME} " +
                "WHERE ${DbFieldNames.FIELD_TASK_ID} = :taskId " +
                "ORDER BY ${BasicLogItem.FIELD_FINISH_TIME} DESC"
    }
}