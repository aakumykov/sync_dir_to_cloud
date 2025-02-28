package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.intersectBy
import com.github.aakumykov.sync_dir_to_cloud.extensions.isSameWith
import com.github.aakumykov.sync_dir_to_cloud.extensions.subtractBy
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository5
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InstructionsGenerator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private var syncObjectReader: SyncObjectReader,
    private val instructionRepository: SyncInstructionRepository5,
    private val instructionCreatorAssistedFactory: SyncInstructionCreatorAssistedFactory,
){
    suspend fun generateSyncInstructions() {

        val sourceObjectsList = syncObjectReader
            .getAllObjectsForTask(SyncSide.SOURCE, syncTask.id)
            .toMutableList()

        val targetObjectsList = syncObjectReader
            .getAllObjectsForTask(SyncSide.TARGET, syncTask.id)
            .toMutableList()

        val both = sourceObjectsList.intersectBy(targetObjectsList, ::areObjectsTheSame)
        val onlyInSource = sourceObjectsList.subtractBy(targetObjectsList, ::areObjectsTheSame)
        val onlyInTarget = targetObjectsList.subtractBy(sourceObjectsList, ::areObjectsTheSame)

        processItemsExistsInSourceAndTarget(both, sourceObjectsList, targetObjectsList)
        processItemsExistsOnlyInSource(onlyInSource)
        processItemsExistsOnlyInTarget(onlyInTarget)
    }

    private fun areObjectsTheSame(o1: SyncObject, o2: SyncObject): Boolean {
        return o1.isSameWith(o2)
    }

    private suspend fun processItemsExistsInSourceAndTarget(
        commonObjectsList: Iterable<SyncObject>,
        sourceObjectsList: Iterable<SyncObject>,
        targetObjectsList: Iterable<SyncObject>
    ) {
        /*for (commonObject in commonObjectsList) {
            instructionRepository.add(
                instructionCreator.createInstructionForSourceAndTargetCommonItems(
                    commonObject,
                    sourceObjectsList.first { it.isSameWith(commonObject) },
                    targetObjectsList.first { it.isSameWith(commonObject) },
                    syncTask.syncMode!!
                )
            )
        }*/
    }


    private suspend fun processItemsExistsOnlyInSource(onlySourceObjectList: Iterable<SyncObject>) {
        /*for(syncObject in onlySourceObjectList) {
            instructionRepository.add(
                instructionCreator.createInstructionForSourceOnlyItem(
                    syncObject,
                    syncTask.syncMode!!
                )
            )
        }*/
    }


    private suspend fun processItemsExistsOnlyInTarget(onlyTargetObjectsList: Iterable<SyncObject>) {
        /*for(syncObject in onlyTargetObjectsList) {
            instructionRepository.add(
                instructionCreator.createInstructionForTargetOnlyItem(
                    syncObject,
                    syncTask.syncMode!!
                )
            )
        }*/
    }


    companion object {
        val TAG: String = InstructionsGenerator::class.java.simpleName
    }
}


@AssistedFactory
interface InstructionsGeneratorAssistedFactory {
    fun create(syncTask: SyncTask): InstructionsGenerator
}