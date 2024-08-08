package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry

@Dao
interface TaskLogDAO {

    @Insert
    suspend fun addTaskLog(taskLogEntry: TaskLogEntry)

    @Query("DELETE FROM task_logs WHERE task_id = :taskId")
    suspend fun deleteEntriesForTask(taskId: String)

    @Query("SELECT * FROM task_logs WHERE task_id = :taskId")
    suspend fun listLogsForTask(taskId: String): List<TaskLogEntry>

    @Query("SELECT * FROM task_logs WHERE task_id = :taskId ORDER BY timestamp DESC")
    fun getLogsForTask(taskId: String): LiveData<List<TaskLogEntry>>
}