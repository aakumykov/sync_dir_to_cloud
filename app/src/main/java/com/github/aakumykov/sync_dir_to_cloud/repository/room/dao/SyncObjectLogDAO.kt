package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState

@Dao
abstract class SyncObjectLogDAO {

    @Insert
    abstract suspend fun addLogItem(item: SyncObjectLogItem)


    fun updateLogItem(item: SyncObjectLogItem) {
        updateLogItemReal(
            objectId = item.objectId,
            taskId = item.taskId,
            executionId = item.executionId,
            timestamp = item.timestamp,
            operationState = item.operationState,
            errorMessage = item.errorMessage
        )
    }

    @Query("UPDATE sync_object_logs SET " +
            "timestamp = :timestamp, " +
            "operation_state = :operationState, " +
            "error_message = :errorMessage " +
            "WHERE object_id = :objectId " +
            "AND task_id = :taskId " +
            "AND execution_id = :executionId")
    protected abstract fun updateLogItemReal(objectId: String,
                                    taskId: String,
                                    executionId: String,
                                    timestamp: Long,
                                    operationState: OperationState,
                                    errorMessage: String?)


    @Query("SELECT * FROM sync_object_logs WHERE " +
            "task_id = :taskId " +
            "AND execution_id = :executionId " +
            "AND object_id = :syncObjectId " +
            "ORDER BY timestamp ASC")
    abstract fun listLogItems(taskId: String, syncObjectId: String, executionId: String): LiveData<List<SyncObjectLogItem>>


    @Query("DELETE FROM sync_object_logs WHERE task_id = :taskId")
    abstract suspend fun deleteLogItemsForTask(taskId: String)


    @Query("SELECT * FROM sync_object_logs WHERE " +
            "task_id = :taskId " +
            "AND execution_id = :executionId " +
            "ORDER BY timestamp ASC")
    abstract fun listAllLogItemsAsLiveData(taskId: String, executionId: String): LiveData<List<SyncObjectLogItem>>


    @Query("SELECT * FROM sync_object_logs WHERE " +
            "task_id = :taskId " +
            "AND execution_id = :executionId " +
            "ORDER BY timestamp ASC")
    abstract suspend fun listAllLogItems(taskId: String, executionId: String): List<SyncObjectLogItem>
}