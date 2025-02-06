package com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_instruction

class BackupedDeleteSyncInstruction(taskId: String,
                                    sourceObjectId: String,
                                    targetObjectId: String,
) : SyncInstruction(
    taskId = taskId,
    execitionId = "executionId",
    sourceObjectId = sourceObjectId,
    targetObjectId = targetObjectId,
    backup = true,
    copy = false,
    delete = true,
)