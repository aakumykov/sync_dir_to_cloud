package com.github.aakumykov.sync_dir_to_cloud.interfaces

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction

interface SyncInstructionReader {
    fun getSyncInstructionsFor(taskId: String): List<SyncInstruction>
}