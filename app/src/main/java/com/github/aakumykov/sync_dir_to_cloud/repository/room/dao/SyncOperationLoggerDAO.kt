package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState

@Dao
interface SyncOperationLoggerDAO {

    @Insert
    suspend fun add(syncOperationLogItem: SyncOperationLogItem)


    @Query("SELECT * FROM sync_operation_logs WHERE " +
            "task_id = :taskId " +
            "AND execution_id = :executionId " +
            "ORDER BY timestamp ASC")
    fun listAsLiveData(taskId: String, executionId: String): LiveData<List<SyncOperationLogItem>>


    @Query("UPDATE sync_operation_logs SET operation_state = :operationState WHERE id = :logItemId")
    suspend fun updateState(logItemId: String, operationState: OperationState)


    @Query("UPDATE sync_operation_logs SET operation_state = :operationState, error_msg = :errorMsg WHERE id = :logItemId")
    suspend fun updateStateAndError(logItemId: String, operationState: OperationState, errorMsg: String)
}
