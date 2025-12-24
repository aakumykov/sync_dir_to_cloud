package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionLogItemType

@Dao
abstract class ExecutionLogDAO {

    @Insert
    abstract suspend fun addItem(taskExecutionLogItem: TaskExecutionLogItem)

    suspend fun updateItem(item: TaskExecutionLogItem) {
        updateItemReal(
            taskId = item.taskId,
            executionId = item.executionId,
            timestamp = item.timestamp,
            type = item.type,
        )
    }

    @Query("UPDATE ${TaskExecutionLogItem.TABLE_NAME} SET timestamp = :timestamp, type = :type WHERE task_id = :taskId AND execution_id = :executionId")
    protected abstract suspend fun updateItemReal(taskId: String, executionId: String, timestamp: Long, type: ExecutionLogItemType)

    @Query("SELECT * FROM ${TaskExecutionLogItem.TABLE_NAME} WHERE task_id = :taskId AND execution_id = :executionId")
    abstract fun getLogsAsLiveData(taskId: String, executionId: String): LiveData<List<TaskExecutionLogItem>>

    @Query("DELETE FROM ${TaskExecutionLogItem.TABLE_NAME}")
    @Deprecated("удалить")
    abstract fun clear()
}