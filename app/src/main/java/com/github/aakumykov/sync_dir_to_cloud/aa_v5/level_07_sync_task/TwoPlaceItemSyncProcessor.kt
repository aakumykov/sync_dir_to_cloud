package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedAndModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedAndNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedAndUnchanged
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isModifiedAndDeleted
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isModifiedAndModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isModifiedAndNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isNewAndDeleted
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isNewAndModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isNewAndNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isNewAndUnchanged
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isUnchangedDeleted
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isUnchangedModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isUnchangedNew
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
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
        processMutuallyUnchangedOrDeleted()
        processSourceUnchangedOrDeletedWithTargetNewOrModified()
        processSourceNewOrModifiedWithTargetUnchangedOrDeleted()
        processSourceDeletedWithTargetUnchanged()
        processSourceUnchangedWithTargetDeleted()
        processNewAndModified()
    }


    //
    private fun processMutuallyUnchangedOrDeleted() {
        // Ничего не делать
    }

    //
    private suspend fun processSourceUnchangedOrDeletedWithTargetNewOrModified() {
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .let { Log.d(TAG, it.toString()); it }
            .filter {
                it.isUnchangedNew ||
                it.isUnchangedModified ||
                it.isDeletedAndNew ||
                it.isDeletedAndModified
            }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6(
                    id = randomUUID,
                    taskId = syncTask.id,
                    executionId = executionId,
                    objectIdInSource = comparisonState.targetObjectId!!,
                    objectIdInTarget = comparisonState.sourceObjectId!!,
                    operation = SyncOperation6.COPY_FROM_TARGET_TO_SOURCE,
                    relativePath = comparisonState.relativePath,
                ))
            }
    }

    //
    private suspend fun processSourceNewOrModifiedWithTargetUnchangedOrDeleted() {
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter {
                it.isNewAndUnchanged ||
                it.isNewAndUnchanged ||
                it.isNewAndDeleted ||
                it.isModifiedAndDeleted
            }
            .let { Log.d(TAG, it.toString()); it }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6(
                    id = randomUUID,
                    taskId = syncTask.id,
                    executionId = executionId,
                    objectIdInSource = comparisonState.targetObjectId!!,
                    objectIdInTarget = comparisonState.sourceObjectId!!,
                    operation = SyncOperation6.COPY_FROM_SOURCE_TO_TARGET,
                    relativePath = comparisonState.relativePath,
                ))
            }
    }

    //
    private suspend fun processSourceDeletedWithTargetUnchanged() {
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter {
                it.isDeletedAndUnchanged
            }
            .let { Log.d(TAG, it.toString()); it }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6(
                    id = randomUUID,
                    taskId = syncTask.id,
                    executionId = executionId,
                    objectIdInSource = comparisonState.targetObjectId!!,
                    objectIdInTarget = comparisonState.sourceObjectId!!,
                    operation = SyncOperation6.DELETE_IN_TARGET,
                    relativePath = comparisonState.relativePath,
                ))
            }
    }

    //
    private suspend fun processSourceUnchangedWithTargetDeleted() {
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter {
                it.isUnchangedDeleted
            }
            .let { Log.d(TAG, it.toString()); it }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6(
                    id = randomUUID,
                    taskId = syncTask.id,
                    executionId = executionId,
                    objectIdInSource = comparisonState.targetObjectId!!,
                    objectIdInTarget = comparisonState.sourceObjectId!!,
                    operation = SyncOperation6.DELETE_IN_SOURCE,
                    relativePath = comparisonState.relativePath,
                ))
            }
    }


    //
    private suspend fun processNewAndModified() {
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter {
                it.isNewAndNew ||
                it.isNewAndModified ||
                it.isModifiedAndNew ||
                it.isModifiedAndModified
            }
            .let { Log.d(TAG, it.toString()); it }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6(
                    id = randomUUID,
                    taskId = syncTask.id,
                    executionId = executionId,
                    objectIdInSource = comparisonState.targetObjectId!!,
                    objectIdInTarget = comparisonState.sourceObjectId!!,
                    operation = SyncOperation6.RENAME_IN_SOURCE,
                    relativePath = comparisonState.relativePath,
                ))
                syncInstructionRepository6.add(SyncInstruction6(
                    id = randomUUID,
                    taskId = syncTask.id,
                    executionId = executionId,
                    objectIdInSource = comparisonState.targetObjectId!!,
                    objectIdInTarget = comparisonState.sourceObjectId!!,
                    operation = SyncOperation6.RENAME_IN_TARGET,
                    relativePath = comparisonState.relativePath,
                ))
                syncInstructionRepository6.add(SyncInstruction6(
                    id = randomUUID,
                    taskId = syncTask.id,
                    executionId = executionId,
                    objectIdInSource = comparisonState.targetObjectId!!,
                    objectIdInTarget = comparisonState.sourceObjectId!!,
                    operation = SyncOperation6.NEED_SECOND_SYNC,
                    relativePath = comparisonState.relativePath,
                ))
            }
    }

    companion object {
        val TAG: String = TwoPlaceItemSyncProcessor::class.java.simpleName
    }
}


@AssistedFactory
interface TwoPlaceSyncItemProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TwoPlaceItemSyncProcessor
}