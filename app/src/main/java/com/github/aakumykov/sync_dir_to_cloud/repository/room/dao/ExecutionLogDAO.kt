package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionLogItemType

@Dao
abstract class ExecutionLogDAO {

    @Insert
    abstract suspend fun addItem(executionLogItem: ExecutionLogItem)

    suspend fun updateItem(item: ExecutionLogItem) {
        updateItemReal(taskId = item.taskId,
            executionId = item.executionId,
            timestamp = item.timestamp,
            type = item.type,
        )
    }

    @Query("UPDATE execution_log SET timestamp = :timestamp, type = :type WHERE task_id = :taskId AND execution_id = :executionId")
    protected abstract suspend fun updateItemReal(taskId: String, executionId: String, timestamp: Long, type: ExecutionLogItemType)

    @Query("SELECT * FROM execution_log WHERE task_id = :taskId AND execution_id = :executionId")
    abstract fun getLogsAsLiveData(taskId: String, executionId: String): LiveData<List<ExecutionLogItem>>

    @Query("DELETE FROM execution_log")
    @Deprecated("удалить")
    abstract fun clear()
}