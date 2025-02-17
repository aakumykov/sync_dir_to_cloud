package com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_instruction

import androidx.room.ColumnInfo
import androidx.room.DeleteColumn
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.ProcessingAction
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.ProcessingSteps
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

@Entity(
    tableName = "sync_instructions",
    primaryKeys = [
        "task_id",
        "source_object_id",
        "target_object_id"
    ],
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
open class SyncInstruction (
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "source_object_id") val sourceObjectId: String,
    @ColumnInfo(name = "target_object_id") val targetObjectId: String,
    @ColumnInfo(name = "is_dir", defaultValue = "false") val isDir: Boolean,
    val backup: Boolean,
    val copy: Boolean,
    val delete: Boolean,
) {
    companion object {
        fun fromProcessingSteps(
            processingSteps: ProcessingSteps,
            taskId: String,
            sourceObjectId: String,
            targetObjectId: String,
        ): SyncInstruction {
            return if (processingSteps.needToBackup) makeSyncInstructionWithBackup(processingSteps.secondAction, taskId, sourceObjectId,targetObjectId)
            else makeSyncInstructionWithoutBackup(processingSteps.secondAction, taskId, sourceObjectId, targetObjectId)
        }

        private fun makeSyncInstructionWithBackup(
            secondProcessingAction: ProcessingAction,
            taskId: String, sourceObjectId: String,
            targetObjectId: String
        ): SyncInstruction {
            return when(secondProcessingAction) {
                ProcessingAction.COPY -> BackupedCopySyncInstruction(taskId, sourceObjectId, targetObjectId)
                ProcessingAction.DELETE -> BackupedDeleteSyncInstruction(taskId, sourceObjectId, targetObjectId)
                else -> DoNothingSyncInstruction(taskId, sourceObjectId, targetObjectId)
            }
        }

        private fun makeSyncInstructionWithoutBackup(
            secondProcessingAction: ProcessingAction,
            taskId: String, sourceObjectId: String,
            targetObjectId: String
        ): SyncInstruction {
            return when(secondProcessingAction) {
                ProcessingAction.COPY -> OnlyCopySyncInstruction(taskId, sourceObjectId, targetObjectId)
                ProcessingAction.DELETE -> OnlyDeleteSyncInstruction(taskId, sourceObjectId, targetObjectId)
                else -> DoNothingSyncInstruction(taskId, sourceObjectId, targetObjectId)
            }
        }
    }

    class FirstAddThisObjectSpec : AutoMigrationSpec {}

    @DeleteColumn(tableName = "sync_instructions", columnName = "execution_id")
    class DeleteColumnExecutionIdSpec : AutoMigrationSpec {}
}
