package com.github.aakumykov.sync_dir_to_cloud.repository

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO6
import javax.inject.Inject

class SyncInstructionRepository6 @Inject constructor(
    private val syncInstructionDAO6: SyncInstructionDAO6,
)
    : SyncInstructionUpdater
{
    suspend fun add(syncInstruction6: SyncInstruction6) {
        syncInstructionDAO6.add(syncInstruction6)
    }

    suspend fun getAllFor(taskId: String, executionId: String): List<SyncInstruction6> {
        return syncInstructionDAO6.getAllFor(taskId, executionId)
    }

    suspend fun deleteFinishedInstructionsForTask(taskId: String) {
        syncInstructionDAO6.deleteFinishedInstructionsForTask(taskId)
    }

    override suspend fun markAsProcessed(instructionId: String) {
        syncInstructionDAO6.markAsProcessed(instructionId)
    }
}