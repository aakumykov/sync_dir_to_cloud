package com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction

@Dao
interface TestSyncInstructionDAO {

    @Query("SELECT * FROM sync_instructions WHERE task_id = :taskId")
    fun list(taskId: String): List<SyncInstruction>
}