package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncOperationLogItem

@Dao
interface SyncOperationLoggerDAO {

    @Insert
    suspend fun add(syncOperationLogItem: SyncOperationLogItem)

    @Query("SELECT * FROM sync_operation_logs WHERE " +
            "task_id = :taskId " +
            "AND execution_id = :executionId " +
            "ORDER BY timestamp ASC")
    suspend fun list(taskId: String, executionId: String): List<SyncOperationLogItem>
}
