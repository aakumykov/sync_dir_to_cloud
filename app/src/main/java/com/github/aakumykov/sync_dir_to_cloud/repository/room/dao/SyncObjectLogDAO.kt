package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem

@Dao
abstract class SyncObjectLogDAO {

    @Insert
    abstract suspend fun addLogItem(item: SyncObjectLogItem)


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