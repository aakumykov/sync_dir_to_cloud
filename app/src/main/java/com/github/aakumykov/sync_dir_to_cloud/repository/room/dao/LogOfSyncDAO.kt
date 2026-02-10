package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.flow.Flow

@Dao
interface LogOfSyncDAO {

    @Query("SELECT * FROM ${LogOfSync.TABLE_NAME} " +
            "WHERE ${GlobalConstants.FIELD_TASK_ID} = :taskId " +
            "AND ${GlobalConstants.FIELD_EXECUTION_ID} = :executionId")
    fun listAsFlow(taskId: String, executionId: String): Flow<List<LogOfSync>>
}