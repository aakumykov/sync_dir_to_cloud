package com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.DbFieldNames
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction

@Dao
interface TestSyncInstructionDAO {

    @Query("SELECT * FROM ${FileInstruction.TABLE_NAME} " +
            "WHERE ${DbFieldNames.FIELD_TASK_ID} = :taskId")
    fun list(taskId: String): List<FileInstruction>
}