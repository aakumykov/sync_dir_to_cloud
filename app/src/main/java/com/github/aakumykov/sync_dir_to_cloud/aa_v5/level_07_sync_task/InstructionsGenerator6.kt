package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InstructionsGenerator6 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val onlyInSourceInstructionGeneratorAssistedFactory: OnlyInSourceInstructionGeneratorAssistedFactory,
    private val onlyInTargetInstructionGeneratorAssistedFactory: OnlyInTargetInstructionGeneratorAssistedFactory,
    private val twoPlaceItemsMirrorInstructionGeneratorAssistedFactory: TwoPlaceItemsMirrorInstructionGeneratorAssistedFactory,
    private val twoPlaceItemsSyncInstructionGeneratorAssistedFactory: TwoPlaceItemsSyncInstructionGeneratorAssistedFactory,
) {
    suspend fun generate() {

        val initialOrderNum = 1
        var nextOrderNum = -1;

        when(syncTask.syncMode!!) {
            SyncMode.SYNC -> {
                nextOrderNum = onlyInSourceInstructionGenerator.generateForSync(initialOrderNum)
                nextOrderNum = onlyInTargetInstructionGenerator.generateForSync(nextOrderNum)
                twoPlaceItemsSyncInstructionGenerator.generate(nextOrderNum)
            }
            SyncMode.MIRROR -> {
                nextOrderNum = onlyInSourceInstructionGenerator.generateForMirror(initialOrderNum)
                nextOrderNum = onlyInTargetInstructionGenerator.generateForSync(nextOrderNum)
                twoPlaceItemsMirrorInstructionGenerator.generate(nextOrderNum)
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
        twoPlaceItemsMirrorInstructionGeneratorAssistedFactory.create(syncTask, executionId)
    }

    private val twoPlaceItemsSyncInstructionGenerator by lazy {
        twoPlaceItemsSyncInstructionGeneratorAssistedFactory.create(syncTask, executionId)
    }
}


@AssistedFactory
interface InstructionsGeneratorAssistedFactory6 {
    fun create(syncTask: SyncTask, executionId: String): InstructionsGenerator6
}