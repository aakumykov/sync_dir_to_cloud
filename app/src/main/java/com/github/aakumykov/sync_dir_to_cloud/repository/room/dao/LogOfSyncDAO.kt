package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.DbFieldNames
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.BasicLogItem
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.flow.Flow

@Dao
interface LogOfSyncDAO {

    @Query(SIMPLE_LIST_QUERY)
    fun listAsFlow(taskId: String, executionId: String): Flow<List<LogOfSync>>


    @Query(SIMPLE_LIST_QUERY)
    suspend fun list(taskId: String, executionId: String): List<LogOfSync>


    @Query("SELECT * FROM ${LogOfSync.TABLE_NAME} " +
            "WHERE ${BasicLogItem.FIELD_LOG_ID} = :origLogId " +
            "AND ${BasicLogItem.FIELD_LOG_ITEM_ABOUT} = :logItemAbout")
    fun get(logItemAbout: LogItemAbout, origLogId: String): LogOfSync?

    companion object {
        const val SIMPLE_LIST_QUERY = "SELECT * FROM ${LogOfSync.TABLE_NAME} " +
                "WHERE ${DbFieldNames.FIELD_TASK_ID} = :taskId " +
                "AND ${DbFieldNames.FIELD_EXECUTION_ID} = :executionId"
    }
}