package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6

@Dao
interface SyncInstructionDAO6 {
    @Insert
    suspend fun add(syncInstruction6: SyncInstruction6)

    @Query("SELECT * FROM sync_instructions_6 " +
            "WHERE task_id = :taskId " +
            "AND execution_id = :executionId")
    suspend fun getAllFor(taskId: String, executionId: String): List<SyncInstruction6>

    @Query("DELETE FROM sync_instructions_6 WHERE task_id = :taskId")
    suspend fun deleteAllForTask(taskId: String)
}