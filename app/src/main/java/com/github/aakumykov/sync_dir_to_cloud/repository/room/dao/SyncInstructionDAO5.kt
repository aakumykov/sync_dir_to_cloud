package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction5
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide

@Dao
interface SyncInstructionDAO5 {

    @Query("SELECT * FROM sync_instructions_5 " +
            "WHERE task_id = :taskId " +
            "AND execution_id = :executionId ")
    suspend fun getSyncInstructions(
        taskId: String,
        executionId: String
    ): List<SyncInstruction5>

    @Insert
    suspend fun add(syncInstruction5: SyncInstruction5)

    @Query("SELECT * FROM sync_instructions_5 " +
            "WHERE task_id = :taskId " +
            "AND execution_id = :executionId " +
            "AND sync_side = :syncSide " +
            "AND is_dir = :isDir")
    suspend fun getSyncInstructions(taskId: String,
                            executionId: String,
                            syncSide: SyncSide,
                            isDir: Boolean): List<SyncInstruction5>

    @Query("DELETE FROM sync_instructions_5 " +
            "WHERE task_id = :taskId")
    suspend fun deleteSyncInstructionsForTask(taskId: String)
}
