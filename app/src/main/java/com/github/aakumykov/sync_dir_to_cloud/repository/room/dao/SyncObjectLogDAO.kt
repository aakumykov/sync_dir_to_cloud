package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem

@Dao
abstract class SyncObjectLogDAO {

    @Transaction
    @Insert
    suspend fun addLogItem(executionId: String, logItem: SyncObjectLogItem) {
        addLogItem(logItem)
        setExecutionId(logItem.objectId, executionId)
    }

    @Insert
    abstract suspend fun addLogItem(item: SyncObjectLogItem)

    @Query("UPDATE sync_object_logs SET execution_id = :executionId WHERE object_id = :objectId")
    abstract suspend fun setExecutionId(objectId:String, executionId: String)


    @Query("SELECT * FROM sync_object_logs WHERE " +
            "task_id = :taskId " +
            "AND execution_id = :executionId " +
            "AND object_id = :syncObjectId " +
            "ORDER BY timestamp ASC")
    abstract fun listLogItems(taskId: String, syncObjectId: String, executionId: String): LiveData<List<SyncObjectLogItem>>


    @Query("DELETE FROM sync_object_logs WHERE task_id = :taskId")
    abstract suspend fun deleteLogItemsForTask(taskId: String)
}