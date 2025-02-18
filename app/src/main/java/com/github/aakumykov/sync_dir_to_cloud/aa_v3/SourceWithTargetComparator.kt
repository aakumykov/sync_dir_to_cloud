package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_instruction.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.isSameWith
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import javax.inject.Inject

class SourceWithTargetComparator @Inject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectAdder: SyncObjectAdder,
    private val syncInstructionRepository: SyncInstructionRepository,
){
    suspend fun removeOldSyncInstructions(taskId: String) {
        syncInstructionRepository.deleteAllFor(taskId)
    }

    suspend fun compare(taskId: String,
                        comparitionStrategy: ComparisionStrategy
    ) {
        val sourceItems: List<SyncObject> = syncObjectReader
            .getAllObjectsForTask(SyncSide.SOURCE, taskId)

        val targetItems: List<SyncObject> = syncObjectReader
            .getAllObjectsForTask(SyncSide.TARGET, taskId)

        for (sourceItem in sourceItems) {

            val stateInSource = sourceItem.stateInStorage

            val stateInTarget: StateInStorage? = targetItems
                .firstOrNull { it.isSameWith(sourceItem) }
                ?.stateInStorage

            val processingSteps = comparitionStrategy.compare(stateInSource, stateInTarget)

            val syncInstruction = SyncInstruction.fromProcessingSteps(
                taskId = taskId,
                sourceObjectId = sourceItem.id,
                targetObjectId = sourceItem.id,
                processingSteps = processingSteps,
            )
            syncInstructionRepository.add(syncInstruction)
        }
    }

}
