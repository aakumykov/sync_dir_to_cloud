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
    private val twoPlaceItemsMirrorProcessorAssistedFactory: TwoPlaceItemInstructionGeneratorAssistedFactory,
    private val twoPlaceItemsSyncProcessorAssistedFactory: TwoPlaceItemsInstructionGeneratorAssistedFactory,
) {
    suspend fun generate() {

        val initialOrderNum = 1

        var nextOrderNum = onlyInSourceItemsProcessor.process(initialOrderNum)

        nextOrderNum = onlyInTargetItemsProcessor.process(nextOrderNum)

        when(syncTask.syncMode!!) {
            SyncMode.SYNC -> twoPlaceSyncItemsProcessor.processSyncing(nextOrderNum)
            SyncMode.MIRROR -> twoPlaceMirrorItemsProcessor.processMirroring(nextOrderNum)
        }
    }

    private val onlyInSourceItemsProcessor by lazy {
        onlyInSourceInstructionGeneratorAssistedFactory.create(syncTask, executionId)
    }

    private val onlyInTargetItemsProcessor by lazy {
        onlyInTargetInstructionGeneratorAssistedFactory.create(syncTask, executionId)
    }

    private val twoPlaceMirrorItemsProcessor by lazy {
        twoPlaceItemsMirrorProcessorAssistedFactory.create(syncTask, executionId)
    }

    private val twoPlaceSyncItemsProcessor by lazy {
        twoPlaceItemsSyncProcessorAssistedFactory.create(syncTask, executionId)
    }
}


@AssistedFactory
interface InstructionsGeneratorAssistedFactory6 {
    fun create(syncTask: SyncTask, executionId: String): InstructionsGenerator6
}