package com.github.aakumykov.sync_dir_to_cloud.sync_task_logger

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskLogDAO {

    @Insert
    suspend fun addTaskLog(taskLogEntry: TaskLogEntry)

    @Query("DELETE FROM task_logs WHERE task_id = :taskId")
    suspend fun deleteEntriesForTask(taskId: String)

    @Query("SELECT * FROM task_logs WHERE task_id = :taskId")
    suspend fun listLogsForTask(taskId: String): List<TaskLogEntry>
}