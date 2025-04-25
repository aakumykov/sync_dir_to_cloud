package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import javax.inject.Inject

class SyncInstructionDeleter6 @Inject constructor(
    private val syncInstructionRepository: SyncInstructionRepository
) {
    suspend fun deleteFinishedInstructionsFor(taskId: String) {
        syncInstructionRepository.deleteFinishedInstructionsForTask(taskId)
    }
}
