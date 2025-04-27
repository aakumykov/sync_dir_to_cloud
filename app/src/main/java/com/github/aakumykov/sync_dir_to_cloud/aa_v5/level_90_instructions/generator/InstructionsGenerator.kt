package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.generator

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InstructionsGenerator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val onlyInSourceInstructionGeneratorAssistedFactory: OnlyInSourceInstructionGeneratorAssistedFactory,
    private val onlyInTargetInstructionGeneratorAssistedFactory: OnlyInTargetInstructionGeneratorAssistedFactory,
    private val twoPlaceInstructionGeneratorForMirrorAssistedFactory: TwoPlaceInstructionGeneratorForMirrorAssistedFactory,
    private val twoPlaceInstructionGeneratorForSyncAssistedFactory: TwoPlaceInstructionGeneratorForSyncAssistedFactory,
) {
    suspend fun generate() {

        var nextOrderNum = 1;

        when(syncTask.syncMode!!) {
            SyncMode.SYNC -> {
                nextOrderNum = onlyInSourceInstructionGenerator.generateForSync(nextOrderNum)
                nextOrderNum = onlyInTargetInstructionGenerator.generateForSync(nextOrderNum)
                nextOrderNum = twoPlaceItemsSyncInstructionGenerator.generate(nextOrderNum)
            }
            SyncMode.MIRROR -> {
                nextOrderNum = onlyInSourceInstructionGenerator.generateForMirror(nextOrderNum)
                nextOrderNum = onlyInTargetInstructionGenerator.generateForMirror(nextOrderNum)
                nextOrderNum = twoPlaceItemsMirrorInstructionGenerator.generate(nextOrderNum)
            }
        }
    }

    private val onlyInSourceInstructionGenerator by lazy {
        onlyInSourceInstructionGeneratorAssistedFactory.create(syncTask, executionId)
    }

    private val onlyInTargetInstructionGenerator by lazy {
        onlyInTargetInstructionGeneratorAssistedFactory.create(syncTask, executionId)
    }

    private val twoPlaceItemsMirrorInstructionGenerator by lazy {
        twoPlaceInstructionGeneratorForMirrorAssistedFactory.create(syncTask, executionId)
    }

    private val twoPlaceItemsSyncInstructionGenerator by lazy {
        twoPlaceInstructionGeneratorForSyncAssistedFactory.create(syncTask, executionId)
    }
}


@AssistedFactory
interface InstructionsGeneratorAssistedFactory6 {
    fun create(syncTask: SyncTask, executionId: String): InstructionsGenerator
}