package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_instruction.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_06_sync_object_list.SyncObjectListChunkedCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncInstructionsProcessor6 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val syncInstructionRepository: SyncInstructionRepository6,
    private val syncObjectListChunkedCopierAssistedFactory5: SyncObjectListChunkedCopierAssistedFactory5,
    private val syncObjectDeleterAssistedFactory5: SyncObjectDeleterAssistedFactory5,
) {
    suspend fun processInstructions() {
        processDirs()
        processFiles()
    }

    private suspend fun processDirs() {

    }

    private suspend fun processFiles() {

    }

    private fun processFileInstruction(syncInstruction: SyncInstruction) {

    }

    private fun processDirInstruction(syncInstruction: SyncInstruction) {

    }
}


@AssistedFactory
interface SyncInstructionsProcessorAssistedFactory6 {
    fun create(syncTask: SyncTask, executionId: String): SyncInstructionsProcessor6
}