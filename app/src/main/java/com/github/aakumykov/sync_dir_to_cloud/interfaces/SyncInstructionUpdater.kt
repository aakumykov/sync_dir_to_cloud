package com.github.aakumykov.sync_dir_to_cloud.interfaces

interface SyncInstructionUpdater {
    suspend fun markAsProcessed(instructionId: String)
}