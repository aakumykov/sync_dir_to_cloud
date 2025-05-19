package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

interface SyncInstructionDeleter {
    suspend fun deleteFinishedInstructionsFor(taskId: String)
}
