package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.enums.PartsLabel
import com.github.aakumykov.sync_dir_to_cloud.enums.FileOperation
import com.github.aakumykov.sync_dir_to_cloud.newRandomId

@Entity(
    tableName = FileInstruction.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.Companion.CASCADE,
            onUpdate = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [
        Index(value = ["task_id"])
    ]
)
class FileInstruction (

    @ColumnInfo(name = "parts_label", defaultValue = "") val partsLabel: PartsLabel,

    @PrimaryKey val id: String,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "execution_id") val executionId: String,

    @ColumnInfo(name = "object_id_in_source") val objectIdInSource: String?,
    @ColumnInfo(name = "object_id_in_target") val objectIdInTarget: String?,

    @Deprecated("не используется")
    @ColumnInfo(name = "order_num", defaultValue = "0") val orderNum: Int,

    @ColumnInfo(name = "operation") val operation: FileOperation,

    @ColumnInfo(name = "is_dir", defaultValue = "false") val isDir: Boolean,
    @ColumnInfo(name = "relative_path", defaultValue = "") val relativePath: String,

    @ColumnInfo(name = "is_processed", defaultValue = "0") val isProcessed: Boolean,
) {
    // TODO: вынести в расширения...

    @Ignore
    val isDeletion: Boolean = FileOperation.DELETE_IN_TARGET == operation ||
            FileOperation.DELETE_IN_SOURCE == operation

    @Ignore
    val isCollisionResolution: Boolean = FileOperation.RESOLVE_COLLISION == operation

    val isBackup: Boolean get() = isBackupInSource || isBackupInTarget

    @Ignore
    val isBackupInSource: Boolean =
        FileOperation.BACKUP_IN_SOURCE == operation

    @Ignore
    val isBackupInTarget: Boolean =
        FileOperation.BACKUP_IN_TARGET == operation

    @Ignore
    val notDeletion: Boolean =
        FileOperation.DELETE_IN_TARGET != operation &&
                FileOperation.DELETE_IN_SOURCE != operation

    @Ignore
    val isCopying: Boolean =
        FileOperation.COPY_FROM_TARGET_TO_SOURCE == operation ||
                FileOperation.COPY_FROM_SOURCE_TO_TARGET == operation

    /*override fun toString(): String {
        return FileInstruction::class.java.simpleName + "{ $operation, $relativePath }"
    }*/

    override fun toString(): String {
        return "FileInstruction(partsLabel=$partsLabel, id='$id', taskId='$taskId', executionId='$executionId', objectIdInSource=$objectIdInSource, objectIdInTarget=$objectIdInTarget, orderNum=$orderNum, operation=$operation, isDir=$isDir, relativePath='$relativePath', isProcessed=$isProcessed, isDeletion=$isDeletion, isCollisionResolution=$isCollisionResolution, isBackup=$isBackup, isBackupInSource=$isBackupInSource, isBackupInTarget=$isBackupInTarget, notDeletion=$notDeletion, isCopying=$isCopying)"
    }

    companion object {
        val TAG: String = FileInstruction::class.java.simpleName

        const val TABLE_NAME = "file_instructions"

        fun from(
            comparisonState: ComparisonState,
            operation: FileOperation,
            partsLabel: PartsLabel,
            orderNum: Int,
        ): FileInstruction = FileInstruction(
            partsLabel = partsLabel,
            id = newRandomId,
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