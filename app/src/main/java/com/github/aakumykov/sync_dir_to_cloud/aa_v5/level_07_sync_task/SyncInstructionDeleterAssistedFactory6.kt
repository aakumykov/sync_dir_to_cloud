package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstructionRepository6
import javax.inject.Inject

class SyncInstructionDeleter6 @Inject constructor(
    private val syncInstructionRepository6: SyncInstructionRepository6
) {
    suspend fun deleteAllFor(taskId: String) {
        syncInstructionRepository6.deleteAllForTask(taskId)
    }
}
