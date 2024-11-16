package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem

@Dao
interface ExecutionLogDAO {

    @Insert
    suspend fun addItem(executionLogItem: ExecutionLogItem)

    @Query("SELECT * FROM execution_log WHERE task_id = :taskId AND execution_id = :executionId")
    fun getLogsAsLiveData(taskId: String, executionId: String): LiveData<List<ExecutionLogItem>>
}