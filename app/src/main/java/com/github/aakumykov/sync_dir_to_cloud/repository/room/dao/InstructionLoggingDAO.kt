package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.InstructionLogItem

@Dao
interface InstructionLoggingDAO {

    @Insert
    fun add(instructionLogItem: InstructionLogItem)


    @Query("SELECT * FROM ${InstructionLogItem.TABLE_NAME} " +
            "WHERE ${InstructionLogItem.FIELD_TASK_ID} = :taskId " +
            "AND ${InstructionLogItem.FIELD_EXECUTION_ID} = :executionId " +
            "ORDER BY ${InstructionLogItem.FIELD_TIMESTAMP}")
    suspend fun list(taskId: String, executionId: String): List<InstructionLogItem>


    @Query("DELETE FROM ${InstructionLogItem.TABLE_NAME} " +
            "WHERE ${InstructionLogItem.FIELD_TASK_ID} = :taskId")
    suspend fun deleteAllForTask(taskId: String)
}