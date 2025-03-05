package com.github.aakumykov.sync_dir_to_cloud.aa_v5.common

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import javax.inject.Inject

@Entity(tableName = "sync_instructions_6")
class SyncInstruction6 (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "execution_id") val executionId: String,

    @ColumnInfo(name = "from_id") val fromId: String,
    @ColumnInfo(name = "to_id") val toId: String,
    @ColumnInfo(name = "operation") val operation6: SyncOperation6,
)


@Dao
interface SyncInstructionDAO6 {
    @Insert
    suspend fun add(syncInstruction6: SyncInstruction6)

    @Query("SELECT * FROM sync_instructions_6 " +
            "WHERE task_id = :taskId " +
            "AND execution_id = :executionId")
    suspend fun getAllFor(taskId: String, executionId: String): List<SyncInstruction6>
}


class SyncInstructionRepository6 @Inject constructor(
    private val syncInstructionDAO6: SyncInstructionDAO6,
) {
    suspend fun add(syncInstruction6: SyncInstruction6) {
        syncInstructionDAO6.add(syncInstruction6)
    }
    suspend fun getAllFor(taskId: String, executionId: String): List<SyncInstruction6> {
        return syncInstructionDAO6.getAllFor(taskId, executionId)
    }
}