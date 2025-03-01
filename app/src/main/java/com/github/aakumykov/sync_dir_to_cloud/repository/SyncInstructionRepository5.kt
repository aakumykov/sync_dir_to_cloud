package com.github.aakumykov.sync_dir_to_cloud.repository;

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction5
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO5
import javax.inject.Inject;

class SyncInstructionRepository5 @Inject constructor(
    private val syncInstructionDAO5: SyncInstructionDAO5
) {
    suspend fun getSyncInstructions(taskId: String,
                                    executionId: String
    ): List<SyncInstruction5> {
        return syncInstructionDAO5.getSyncInstructions(taskId, executionId)
    }

    suspend fun add(syncInstruction5: SyncInstruction5) {
        syncInstructionDAO5.add(syncInstruction5)
    }

    suspend fun deleteSyncInstructionsForTask(taskId: String) {
        syncInstructionDAO5.deleteSyncInstructionsForTask(taskId)
    }
}
