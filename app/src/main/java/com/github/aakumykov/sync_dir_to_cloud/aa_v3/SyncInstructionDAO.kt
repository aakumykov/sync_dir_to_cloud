package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_instruction.SyncInstruction

@Dao
interface SyncInstructionDAO {

    @Insert
    suspend fun addSyncInstruction(syncInstruction: SyncInstruction)


    @Query("DELETE from sync_instructions " +
            "WHERE task_id = :taskId ")
    suspend fun deleteAllFor(taskId: String)


    @Query("SELECT * FROM sync_instructions WHERE task_id = :taskId ")
    suspend fun get(taskId: String): List<SyncInstruction>
}