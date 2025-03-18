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
    private val onlyInTargetInstructionGeneratorForSyncAssistedFactory: OnlyInTargetInstructionGeneratorForSyncAssistedFactory,
    private val twoPlaceItemsMirrorInstructionGeneratorAssistedFactory: TwoPlaceItemsMirrorInstructionGeneratorAssistedFactory,
    private val twoPlaceItemsSyncInstructionGeneratorAssistedFactory: TwoPlaceItemsSyncInstructionGeneratorAssistedFactory,
) {
    suspend fun generate() {

        val initialOrderNum = 1
        var nextOrderNum = -1;

        when(syncTask.syncMode!!) {
            SyncMode.SYNC -> {
                // В режиме SYNC приёмник не обрабатывается.
                // Да? А как же бекап?
                // Получается, нужно обрабатывать.
                nextOrderNum = onlyInSourceInstructionGenerator.generateForSync(initialOrderNum)
                twoPlaceItemsSyncInstructionGenerator.generate(nextOrderNum)
            }
            SyncMode.MIRROR -> {
                nextOrderNum = onlyInSourceInstructionGenerator.generateForSync(initialOrderNum)
                nextOrderNum = onlyInTargetInstructionGenerator.generate(nextOrderNum)
                twoPlaceItemsMirrorInstructionGenerator.generate(nextOrderNum)
            }
        }
    }

    private val onlyInSourceInstructionGenerator by lazy {
        onlyInSourceInstructionGeneratorAssistedFactory.create(syncTask, executionId)
    }

    private val onlyInTargetInstructionGenerator by lazy {
        onlyInTargetInstructionGeneratorForSyncAssistedFactory.create(syncTask, executionId)
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