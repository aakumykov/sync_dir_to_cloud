package com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_instruction

class DoNothingSyncInstruction(taskId: String,
                               sourceObjectId: String,
                               targetObjectId: String,
) : SyncInstruction(
    taskId = taskId,
    sourceObjectId = sourceObjectId,
    targetObjectId = targetObjectId,
    backup = false,
    copy = false,
    delete = false,
)