package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_08_instructions

import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import javax.inject.Inject

class SyncInstructionDeleter @Inject constructor(
    private val syncInstructionRepository: SyncInstructionRepository
) {
    suspend fun deleteFinishedInstructionsFor(taskId: String) {
        syncInstructionRepository.deleteFinishedInstructionsForTask(taskId)
    }
}
