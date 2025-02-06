package com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_instruction

class BackupedCopySyncInstruction(taskId: String,
                                  sourceObjectId: String,
                                    targetObjectId: String,
) : SyncInstruction(
    taskId = taskId,
    sourceObjectId = sourceObjectId,
    targetObjectId = targetObjectId,
    backup = true,
    copy = true,
    delete = false,
)