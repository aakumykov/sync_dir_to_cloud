package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry

@Dao
interface SyncTaskLogDAO {

    @Insert
    suspend fun addTaskLog(taskLogEntry: TaskLogEntry)

    @Query("DELETE FROM ${TABLE_NAME} WHERE task_id = :taskId")
    suspend fun deleteEntriesForTask(taskId: String)

    @Query("SELECT * FROM ${TABLE_NAME} WHERE task_id = :taskId")
    suspend fun listLogsForTask(taskId: String): List<TaskLogEntry>

    @Query("SELECT * FROM ${TABLE_NAME} WHERE task_id = :taskId ORDER BY start_time DESC")
    fun getLogsForTask(taskId: String): LiveData<List<TaskLogEntry>>


    @Query("UPDATE $TABLE_NAME " +
            "SET " +
            "finish_time = :finishTime, " +
            "entry_type = 'FINISH' " +
            "WHERE " +
            "task_id = :taskId AND " +
            "execution_id = :executionId")
    suspend fun updateAsSuccess(taskId: String, executionId: String, finishTime: Long)


    @Query("UPDATE $TABLE_NAME " +
            "SET " +
            "finish_time = :finishTime, " +
            "entry_type = 'ERROR', " +
            "error_msg = :errorMsg " +
            "WHERE " +
            "task_id = :taskId AND " +
            "execution_id = :executionId")
    suspend fun updateAsError(taskId: String, executionId: String, finishTime: Long, errorMsg: String?)


    companion object {
        const val TABLE_NAME = TaskLogEntry.TABLE_NAME
    }
}