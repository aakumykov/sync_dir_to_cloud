package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction

@Dao
interface SyncInstructionDAO {
    @Insert
    suspend fun add(syncInstruction: SyncInstruction)


    @Query("SELECT * FROM ${SyncInstruction.TABLE_NAME} " +
            "WHERE task_id = :taskId " +
            "AND execution_id = :executionId")
    suspend fun getAllFor(taskId: String, executionId: String): List<SyncInstruction>


    @Query("SELECT * FROM ${SyncInstruction.TABLE_NAME} " +
            "WHERE task_id = :taskId")
    suspend fun getAllWithoutExecutionId(taskId: String): List<SyncInstruction>


    @Query("DELETE FROM ${SyncInstruction.TABLE_NAME} WHERE " +
            "task_id = :taskId AND " +
            "is_processed = '1'")
    suspend fun deleteFinishedInstructionsForTask(taskId: String)


    @Query("UPDATE ${SyncInstruction.TABLE_NAME} SET is_processed = '1' WHERE id = :instructionId")
    suspend fun markAsProcessed(instructionId: String)

    @Query("DELETE FROM ${SyncInstruction.TABLE_NAME} WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM ${SyncInstruction.TABLE_NAME} WHERE task_id = :taskId")
    fun getSyncInstructionsFor(taskId: String): List<SyncInstruction>


    @Query("SELECT * FROM ${SyncInstruction.TABLE_NAME} WHERE object_id_in_source = :objectId")
    fun getSyncInstructionsForSourceId(objectId: String): List<SyncInstruction>
}