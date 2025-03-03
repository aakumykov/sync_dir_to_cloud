package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StoragePriority
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository5
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class InstructionsGenerator5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val instructionCreatorAssistedFactory5: InstructionCreatorAssistedFactory5,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository: SyncInstructionRepository5,
) {
    suspend fun generateSyncInstructions() {
        comparisonStateRepository.getAllFor(syncTask.id, executionId)
            .forEach { comparisonState ->
                syncInstructionRepository.add(
                    instructionCreator5.createSyncInstruction(
                        comparisonState = comparisonState,
                        syncMode = syncTask.syncMode!!,
                        storagePriority = StoragePriority.PRIORITY_OF_SOURCE,
                        withBackup = syncTask.withBackup,
                        onlyAdd = false,
                    )
                )
            }
    }

    private val instructionCreator5: InstructionCreator5 by lazy {
        instructionCreatorAssistedFactory5.create(syncTask, executionId)
    }
}