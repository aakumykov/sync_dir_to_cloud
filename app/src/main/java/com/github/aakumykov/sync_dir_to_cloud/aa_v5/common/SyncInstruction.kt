package com.github.aakumykov.sync_dir_to_cloud.aa_v5.common

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.randomUUID

@Entity(
    tableName = "sync_instructions",
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
class SyncInstruction (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "execution_id") val executionId: String,

    @Deprecated("избавиться от ?")
    @ColumnInfo(name = "object_id_in_source") val objectIdInSource: String?,

    @Deprecated("избавиться от ?")
    @ColumnInfo(name = "object_id_in_target") val objectIdInTarget: String?,

    @ColumnInfo(name = "order_num", defaultValue = "0") val orderNum: Int,
    @ColumnInfo(name = "operation") val operation: SyncOperation6,

    @ColumnInfo(name = "is_dir", defaultValue = "false") val isDir: Boolean,
    @ColumnInfo(name = "relative_path", defaultValue = "") val relativePath: String,

    @ColumnInfo(name = "is_processed", defaultValue = "0") val isProcessed: Boolean,
) {
    @Ignore val isDeletion: Boolean = SyncOperation6.DELETE_IN_TARGET == operation ||
            SyncOperation6.DELETE_IN_SOURCE == operation

    @Ignore val isCollisionResolution: Boolean = SyncOperation6.RESOLVE_COLLISION == operation

    @Ignore val notDeletion: Boolean =
        SyncOperation6.DELETE_IN_TARGET != operation &&
                SyncOperation6.DELETE_IN_SOURCE != operation

    @Ignore val isCopying: Boolean =
        SyncOperation6.COPY_FROM_TARGET_TO_SOURCE == operation ||
                SyncOperation6.COPY_FROM_SOURCE_TO_TARGET == operation

    override fun toString(): String {
        return SyncInstruction::class.java.simpleName + "{ $operation, $relativePath }"
    }

    @RenameColumn(
        tableName = "sync_instructions_6",
        fromColumnName = "from_id",
        toColumnName = "object_id_in_source")
    @RenameColumn(
        tableName = "sync_instructions_6",
        fromColumnName = "to_id",
        toColumnName = "object_id_in_target")
    class RenameObjectIdColumnsMigration1: AutoMigrationSpec

    companion object {
        fun from(
            comparisonState: ComparisonState,
            operation: SyncOperation6,
            orderNum: Int,
        ): SyncInstruction = SyncInstruction(
            id = randomUUID,
            taskId = comparisonState.taskId,
            executionId = comparisonState.executionId,
            objectIdInSource = comparisonState.sourceObjectId,
            objectIdInTarget = comparisonState.targetObjectId,
            operation = operation,
            isDir = comparisonState.isDir,
            relativePath = comparisonState.relativePath,
            orderNum = orderNum,
            isProcessed = false,
        )
    }
}