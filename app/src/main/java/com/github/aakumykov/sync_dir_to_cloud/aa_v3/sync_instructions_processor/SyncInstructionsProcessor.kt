package com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_instructions_processor

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.ItemBackuperAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.ItemCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.ItemDeleter
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncInstructionRepository
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncObjectLogRepository3AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_instruction.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncInstructionsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,

    private val instructionRepository: SyncInstructionRepository,
    private val syncObjectReader: SyncObjectReader,

    private val itemBackuperAssistedFactory: ItemBackuperAssistedFactory,
    private val deleter: ItemDeleter,
    private val itemCopierAssistedFactory: ItemCopierAssistedFactory,

    private val syncObjectLogRepository3AssistedFactory: SyncObjectLogRepository3AssistedFactory
) {
    suspend fun processSyncInstructions() {
        instructionRepository.getAllFor(syncTask.id).forEach { syncInstruction ->
            processOneInstruction(syncInstruction)
        }
    }

    private suspend fun processOneInstruction(syncInstruction: SyncInstruction) {
        val sourceObject = syncObjectReader.getSyncObject(syncInstruction.sourceObjectId)
        val targetObject = syncObjectReader.getSyncObject(syncInstruction.targetObjectId)

        if (syncInstruction.backup) makeBackup(targetObject)
        if (syncInstruction.copy) makeCopy(sourceObject, targetObject)
        if (syncInstruction.delete) makeDelete(targetObject)
    }

    private suspend fun makeBackup(targetObject: SyncObject?) {
        itemBackuperAssistedFactory
            .create(syncTask, executionId)
            .process(targetObject)
    }

    private suspend fun makeCopy(sourceObject: SyncObject?, targetObject: SyncObject?) {
        itemCopierAssistedFactory.create(
            syncTask,
            syncObjectLogRepository3AssistedFactory.create(syncTask.id, executionId)
        ).process(sourceObject, targetObject)
    }

    private suspend fun makeDelete(targetObject: SyncObject?) {
        deleter.process(targetObject)
    }
}

@AssistedFactory
interface SyncInstructionsProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): SyncInstructionsProcessor
}