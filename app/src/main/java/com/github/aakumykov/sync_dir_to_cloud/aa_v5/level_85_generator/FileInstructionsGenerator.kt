package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_85_generator

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FileInstructionsGenerator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val onlyInSourceInstructionGeneratorAssistedFactory: OnlyInSourceInstructionGeneratorAssistedFactory,
    private val onlyInTargetInstructionGeneratorAssistedFactory: OnlyInTargetInstructionGeneratorAssistedFactory,
    private val twoPlaceInstructionGeneratorForMirrorAssistedFactory: TwoPlaceInstructionGeneratorForMirrorAssistedFactory,
    private val twoPlaceInstructionGeneratorForSyncAssistedFactory: TwoPlaceInstructionGeneratorForSyncAssistedFactory,
) {
    suspend fun generateFileInstructions() {
        Log.d(TAG, "generate()")

        var nextOrderNum = 1;

        when(syncTask.syncMode!!) {
            SyncMode.SYNC -> {
                nextOrderNum = onlyInSourceInstructionGenerator.generateFileInstructionsForSync(nextOrderNum)
                nextOrderNum = onlyInTargetInstructionGenerator.generateFileInstructionsForSync(nextOrderNum)
                nextOrderNum = twoPlaceItemsSyncInstructionGenerator.generateFileInstructions(nextOrderNum)
            }
            SyncMode.MIRROR -> {
                nextOrderNum = onlyInSourceInstructionGenerator.generateFileInstructionsForMirror(nextOrderNum)
                nextOrderNum = onlyInTargetInstructionGenerator.generateFileInstructionsForMirror(nextOrderNum)
                nextOrderNum = twoPlaceItemsMirrorInstructionGenerator.generateFileInstructions(nextOrderNum)
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

    companion object {
        val TAG: String = FileInstructionsGenerator::class.java.simpleName
    }
}


@AssistedFactory
interface InstructionsGeneratorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): FileInstructionsGenerator
}