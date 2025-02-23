package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.intersectBy
import com.github.aakumykov.sync_dir_to_cloud.extensions.isSameWith
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository5
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InstructionsGenerator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private var syncObjectReader: SyncObjectReader,
    private val instructionRepository: SyncInstructionRepository5,
    private val instructionCreator: SyncInstructionCreator,
){
    suspend fun generateSyncInstructions() {

        val sourceObjectsList = syncObjectReader
            .getAllObjectsForTask(SyncSide.SOURCE, syncTask.id)
            .toMutableList()

        val targetObjectsList = syncObjectReader
            .getAllObjectsForTask(SyncSide.TARGET, syncTask.id)
            .toMutableList()

        processItemsExistsInSourceAndTartet(sourceObjectsList, targetObjectsList)
        processItemsExistsOnlyInSource(sourceObjectsList, targetObjectsList)
        processItemsExistsOnlyInTarget(sourceObjectsList, targetObjectsList)
    }


    private fun processItemsExistsInSourceAndTartet(
        sourceObjectsList: MutableList<SyncObject>,
        targetObjectsList: MutableList<SyncObject>
    ) {
        sourceObjectsList.intersectBy(targetObjectsList) { i1,i2 -> i1.isSameWith(i2) }
            .forEach { syncObject ->

            }
    }


    private fun processItemsExistsOnlyInSource(
        sourceObjectsList: MutableList<SyncObject>,
        targetObjectsList: MutableList<SyncObject>
    ) {

    }


    private fun processItemsExistsOnlyInTarget(
        sourceObjectsList: MutableList<SyncObject>,
        targetObjectsList: MutableList<SyncObject>
    ) {

    }


    private suspend fun processTwoObjectLists(
        firstList: List<SyncObject>,
        secondList: List<SyncObject>,
    ) {
        val fistWithoutSecond = firstList.toMutableList()

        fistWithoutSecond.removeAll { firstSyncObject ->
            secondList.firstOrNull { secondSyncObject ->
                firstSyncObject.isSameWith(secondSyncObject)
            }?.let { true } ?: false
        }

        Log.d(TAG, fistWithoutSecond.toString())
    }

    companion object {
        val TAG: String = InstructionsGenerator::class.java.simpleName
    }
}


@AssistedFactory
interface InstructionsGeneratorAssistedFactory {
    fun create(syncTask: SyncTask): InstructionsGenerator
}