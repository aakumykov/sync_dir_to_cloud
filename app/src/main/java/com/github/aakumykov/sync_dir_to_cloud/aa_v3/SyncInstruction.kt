package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec

@Entity(
    tableName = "sync_instructions",
    primaryKeys = [
        "task_id",
        "execution_id",
        "source_object_id",
        "target_object_id"
    ]
)
open class SyncInstruction (
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "execution_id") val execitionId: String,
    @ColumnInfo(name = "source_object_id") val sourceObjectId: String,
    @ColumnInfo(name = "target_object_id") val targetObjectId: String,
    val backup: Boolean,
    val copy: Boolean,
    val delete: Boolean,
) {
    companion object {
        fun fromProcessingSteps(
            processingSteps: ProcessingSteps,
            taskId: String,
            executionId: String,
            sourceObjectId: String,
            targetObjectId: String,
        ): SyncInstruction {
            return if (processingSteps.needToBackup) withBackup(processingSteps.secondAction, taskId,executionId,sourceObjectId,targetObjectId)
            else withoutBackup(processingSteps.secondAction, taskId,executionId,sourceObjectId,targetObjectId)
        }

        private fun withBackup(
            secondProcessingAction: ProcessingAction,
            taskId: String,
            executionId: String,
            sourceObjectId: String,
            targetObjectId: String
        ): SyncInstruction {
            return when(secondProcessingAction) {
                ProcessingAction.COPY -> BackupedCopySyncInstruction(taskId, executionId, sourceObjectId, targetObjectId)
                ProcessingAction.DELETE -> BackupedDeleteSyncInstruction(taskId, executionId, sourceObjectId, targetObjectId)
                else -> DoNothingSyncInstruction(taskId, executionId, sourceObjectId, targetObjectId)
            }
        }

        private fun withoutBackup(
            secondProcessingAction: ProcessingAction,
            taskId: String,
            executionId: String,
            sourceObjectId: String,
            targetObjectId: String
        ): SyncInstruction {
            return when(secondProcessingAction) {
                ProcessingAction.COPY -> SimpleCopySyncInstruction(taskId, executionId, sourceObjectId, targetObjectId)
                ProcessingAction.DELETE -> SimpleDeleteSyncInstruction(taskId, executionId, sourceObjectId, targetObjectId)
                else -> DoNothingSyncInstruction(taskId, executionId, sourceObjectId, targetObjectId)
            }
        }
    }

    class FirstAddThisObjectSpec : AutoMigrationSpec {

    }
}


class DoNothingSyncInstruction(taskId: String,
                               executionId: String,
                               sourceObjectId: String,
                               targetObjectId: String,
) : SyncInstruction(
    taskId = taskId,
    execitionId = executionId,
    sourceObjectId = sourceObjectId,
    targetObjectId = targetObjectId,
    backup = false,
    copy = false,
    delete = false,
)

class SimpleCopySyncInstruction(taskId: String,
                                executionId: String,
                                sourceObjectId: String,
                                targetObjectId: String,
) : SyncInstruction(
    taskId = taskId,
    execitionId = executionId,
    sourceObjectId = sourceObjectId,
    targetObjectId = targetObjectId,
    backup = false,
    copy = true,
    delete = false,
)


class SimpleDeleteSyncInstruction(taskId: String,
                                    executionId: String,
                                    sourceObjectId: String,
                                    targetObjectId: String,
) : SyncInstruction(
    taskId = taskId,
    execitionId = executionId,
    sourceObjectId = sourceObjectId,
    targetObjectId = targetObjectId,
    backup = false,
    copy = false,
    delete = true,
)

class BackupedCopySyncInstruction(taskId: String,
                                  executionId: String,
                                  sourceObjectId: String,
                                    targetObjectId: String,
) : SyncInstruction(
    taskId = taskId,
    execitionId = executionId,
    sourceObjectId = sourceObjectId,
    targetObjectId = targetObjectId,
    backup = true,
    copy = true,
    delete = false,
)

class BackupedDeleteSyncInstruction(taskId: String,
                                    executionId: String,
                                    sourceObjectId: String,
                                    targetObjectId: String,
) : SyncInstruction(
    taskId = taskId,
    execitionId = executionId,
    sourceObjectId = sourceObjectId,
    targetObjectId = targetObjectId,
    backup = true,
    copy = false,
    delete = true,
)