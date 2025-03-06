package com.github.aakumykov.sync_dir_to_cloud.aa_v5.common

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import javax.inject.Inject

@Entity(
    tableName = "sync_instructions_6",
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
)
class SyncInstruction6 (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "execution_id") val executionId: String,

    @ColumnInfo(name = "object_id_in_source") val objectIdInSource: String?,
    @ColumnInfo(name = "object_id_in_target") val objectIdInTarget: String?,

    @ColumnInfo(name = "operation") val operation: SyncOperation6,

    @ColumnInfo(name = "relative_path", defaultValue = "") val relativePath: String
) {
    @RenameColumn(
        tableName = "sync_instructions_6",
        fromColumnName = "from_id",
        toColumnName = "object_id_in_source")
    @RenameColumn(
        tableName = "sync_instructions_6",
        fromColumnName = "to_id",
        toColumnName = "object_id_in_target")
    class RenameObjectIdColumnsMigration1: AutoMigrationSpec
    companion object
}


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


fun SyncInstruction6.Companion.from(
    comparisonState: ComparisonState,
    operation: SyncOperation6
): SyncInstruction6 = SyncInstruction6(
    id = randomUUID,
    taskId = comparisonState.taskId,
    executionId = comparisonState.executionId,
    objectIdInSource = comparisonState.sourceObjectId,
    objectIdInTarget = comparisonState.targetObjectId,
    operation = operation,
    relativePath = comparisonState.relativePath
)