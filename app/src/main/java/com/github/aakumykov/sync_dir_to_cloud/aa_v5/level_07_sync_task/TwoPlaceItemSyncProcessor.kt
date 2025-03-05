package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isUnchangedNew
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StoragePriority
import com.github.aakumykov.sync_dir_to_cloud.extensions.isSameWith
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TwoPlaceItemSyncProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository6: SyncInstructionRepository6,
) {
    suspend fun process() {

        // Прежние в источнике
        processUnchangedInSourceAndUnchangedInTarget()
        processUnchangedInSourceAndNewInTarget()
        processUnchangedInSourceAndModifiedInTarget()
        processUnchangedInSourceAndDeletedInTarget()

        // Новые в источнике
        processNewInSourceAndUnchangedInTarget()
        processNewInSourceAndNewInTarget()
        processNewInSourceAndModifiedInTarget()
        processNewInSourceAndDeletedInTarget()

        // Изменившиеся в источнике
        processModifiedInSourceAndUnchangedInTarget()
        processModifiedInSourceAndNewInTarget()
        processModifiedInSourceAndModifiedInTarget()
        processModifiedInSourceAndDeletedInTarget()

        // Удалённые в источнике
        processDeletedInSourceAndUnchangedInTarget()
        processDeletedInSourceAndNewInTarget()
        processDeletedInSourceAndModifiedInTarget()
        processDeletedInSourceAndDeletedInTarget()
    }


    private fun processUnchangedInSourceAndUnchangedInTarget() {
        // Ничего не делать
    }

    private suspend fun processUnchangedInSourceAndNewInTarget() {
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter { it.isUnchangedNew }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6(
                    id = randomUUID,
                    taskId = syncTask.id,
                    executionId = executionId,
                    objectIdInSource = comparisonState.targetObjectId!!,
                    objectIdInTarget = comparisonState.sourceObjectId!!,
                    operation = SyncOperation6.COPY_FROM_TARGET_TO_SOURCE,
                ))
            }
    }

    private fun processUnchangedInSourceAndModifiedInTarget() {

    }

    private fun processUnchangedInSourceAndDeletedInTarget() {

    }


    private fun processNewInSourceAndUnchangedInTarget() {

    }

    private fun processNewInSourceAndNewInTarget() {

    }

    private fun processNewInSourceAndModifiedInTarget() {

    }

    private fun processNewInSourceAndDeletedInTarget() {

    }


    private fun processModifiedInSourceAndUnchangedInTarget() {

    }

    private fun processModifiedInSourceAndNewInTarget() {

    }

    private fun processModifiedInSourceAndModifiedInTarget() {

    }

    private fun processModifiedInSourceAndDeletedInTarget() {

    }


    private fun processDeletedInSourceAndUnchangedInTarget() {

    }

    private fun processDeletedInSourceAndNewInTarget() {

    }

    private fun processDeletedInSourceAndModifiedInTarget() {

    }

    private fun processDeletedInSourceAndDeletedInTarget() {

    }
}


@AssistedFactory
interface TwoPlaceSyncItemProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TwoPlaceItemSyncProcessor
}