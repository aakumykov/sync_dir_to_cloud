package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedAndModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedAndNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedAndUnchanged
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isModifiedAndDeleted
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isModifiedAndModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isModifiedAndNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isModifiedAndUnchanged
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isNewAndDeleted
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isNewAndModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceNewAndTargetNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceNewAndTargetUnchanged
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceUnchangedTargetDeleted
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceUnchangedTargetModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceUnchangedTargetNew
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TwoPlaceItemInstructionGenerator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository6: SyncInstructionRepository6,
) {
    suspend fun processMirroring(initialOrderNum: Int): Int {
        var nextOrderNum = processMutuallyUnchangedOrDeleted(initialOrderNum)
        nextOrderNum = processSourceUnchangedOrDeletedWithTargetNewOrModified(nextOrderNum)
        nextOrderNum = processSourceNewOrModifiedWithTargetUnchangedOrDeleted(nextOrderNum)
        nextOrderNum = processSourceDeletedWithTargetUnchanged(nextOrderNum)
        nextOrderNum = processSourceUnchangedWithTargetDeleted(nextOrderNum)
        return processNewAndModified(nextOrderNum)
    }


    //
    private fun processMutuallyUnchangedOrDeleted(initialOrderNum: Int): Int {
        return initialOrderNum
    }

    //
    private suspend fun processSourceUnchangedOrDeletedWithTargetNewOrModified(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getAllComparisonStatesFor(syncTask.id, executionId)
            .let { Log.d(TAG, it.toString()); it }
            .filter {
                it.isSourceUnchangedTargetNew ||
                it.isSourceUnchangedTargetModified ||
                it.isDeletedAndNew ||
                it.isDeletedAndModified
            }
            .let { Log.d(TAG, it.toString()); it }
            .forEach { comparisonState ->
                syncInstructionRepository6.apply {
                    add(SyncInstruction6.from(
                        comparisonState = comparisonState,
                        operation = SyncOperation6.COPY_FROM_TARGET_TO_SOURCE,
                        orderNum = n++
                    ))
                }
            }
        return n
    }

    //
    private suspend fun processSourceNewOrModifiedWithTargetUnchangedOrDeleted(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getAllComparisonStatesFor(syncTask.id, executionId)
            .filter {
                it.isSourceNewAndTargetUnchanged ||
                it.isNewAndDeleted ||
                it.isModifiedAndUnchanged ||
                it.isModifiedAndDeleted
            }
            .let { Log.d(TAG, it.toString()); it }
            .forEach { comparisonState ->
                syncInstructionRepository6.apply {
                    add(SyncInstruction6.from(
                        comparisonState = comparisonState,
                        operation = SyncOperation6.COPY_FROM_SOURCE_TO_TARGET,
                        orderNum = n++
                    ))
                }
            }
        return n
    }

    //
    private suspend fun processSourceDeletedWithTargetUnchanged(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getAllComparisonStatesFor(syncTask.id, executionId)
            .filter {
                it.isDeletedAndUnchanged
            }
            .let { Log.d(TAG, it.toString()); it }
            .forEach { comparisonState ->
                syncInstructionRepository6.apply {
                    if (syncTask.withBackup) {
                        add(SyncInstruction6.from(
                            comparisonState = comparisonState,
                            operation = SyncOperation6.BACKUP_IN_TARGET,
                            orderNum = n++
                        ))
                    }
                    add(SyncInstruction6.from(
                        comparisonState = comparisonState,
                        operation = SyncOperation6.DELETE_IN_TARGET,
                        orderNum = n++
                    ))
                }
            }
        return n
    }

    //
    private suspend fun processSourceUnchangedWithTargetDeleted(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getAllComparisonStatesFor(syncTask.id, executionId)
            .filter {
                it.isSourceUnchangedTargetDeleted
            }
            .let { Log.d(TAG, it.toString()); it }
            .forEach { comparisonState ->
                syncInstructionRepository6.apply {
                    if (syncTask.withBackup) {
                        add(SyncInstruction6.from(
                            comparisonState = comparisonState,
                            operation = SyncOperation6.BACKUP_IN_SOURCE,
                            orderNum = n++
                        ))
                    }
                    add(SyncInstruction6.from(
                        comparisonState = comparisonState,
                        operation = SyncOperation6.DELETE_IN_SOURCE,
                        orderNum = n++
                    ))
                }
            }
        return n
    }


    //
    private suspend fun processNewAndModified(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getAllComparisonStatesFor(syncTask.id, executionId)
            .filter {
                it.isSourceNewAndTargetNew ||
                it.isNewAndModified ||
                it.isModifiedAndNew ||
                it.isModifiedAndModified
            }
            .let { Log.d(TAG, it.toString()); it }
            .forEach { comparisonState ->
                syncInstructionRepository6.apply {
                    add(SyncInstruction6.from(
                        comparisonState = comparisonState,
                        operation = SyncOperation6.RENAME_COLLISION_IN_SOURCE,
                        orderNum = n++
                    ))
                    add(SyncInstruction6.from(
                        comparisonState = comparisonState,
                        operation = SyncOperation6.RENAME_COLLISION_IN_TARGET,
                        orderNum = n++
                    ))
                    add(SyncInstruction6.from(
                        comparisonState = comparisonState,
                        operation = SyncOperation6.COPY_FROM_SOURCE_TO_TARGET,
                        orderNum = n++
                    ))
                    add(SyncInstruction6.from(
                        comparisonState = comparisonState,
                        operation = SyncOperation6.COPY_FROM_TARGET_TO_SOURCE,
                        orderNum = n++
                    ))
                }
            }
        return n
    }


    private suspend fun getAllComparisonStatesFor(taskId: String, executionId: String): Iterable<ComparisonState>
        = comparisonStateRepository.getAllFor(taskId, executionId)

    companion object {
        val TAG: String = TwoPlaceItemInstructionGenerator::class.java.simpleName
    }
}


@AssistedFactory
interface TwoPlaceItemInstructionGeneratorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TwoPlaceItemInstructionGenerator
}