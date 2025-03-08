package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.from
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
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TwoPlaceItemMirrorProcessor @AssistedInject constructor(
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
                syncInstructionRepository6.add(SyncInstruction6.from(
                    comparisonState = comparisonState,
                    operation = SyncOperation6.COPY_FROM_TARGET_TO_SOURCE,
                    orderNum = n++
                ))
            }
        return n
    }

    //
    private suspend fun processSourceNewOrModifiedWithTargetUnchangedOrDeleted(initialOrderNum: Int): Int {
        var n = initialOrderNum
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
                syncInstructionRepository6.add(SyncInstruction6.from(
                    comparisonState = comparisonState,
                    operation = SyncOperation6.COPY_FROM_SOURCE_TO_TARGET,
                    orderNum = n++
                ))
            }
        return n
    }

    //
    private suspend fun processSourceDeletedWithTargetUnchanged(initialOrderNum: Int): Int {
        var n = initialOrderNum
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter {
                it.isDeletedAndUnchanged
            }
//            .let { Log.d(TAG, it.toString()); it }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6.from(
                    comparisonState = comparisonState,
                    operation = SyncOperation6.DELETE_IN_TARGET,
                    orderNum = n++
                ))
            }
        return n
    }

    //
    private suspend fun processSourceUnchangedWithTargetDeleted(initialOrderNum: Int): Int {
        var n = initialOrderNum
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter {
                it.isUnchangedDeleted
            }
//            .let { Log.d(TAG, it.toString()); it }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6.from(
                    comparisonState = comparisonState,
                    operation = SyncOperation6.DELETE_IN_SOURCE,
                    orderNum = n++
                ))
            }
        return n
    }


    //
    private suspend fun processNewAndModified(initialOrderNum: Int): Int {
        var n = initialOrderNum
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter {
                it.isNewAndNew ||
                it.isNewAndModified ||
                it.isModifiedAndNew ||
                it.isModifiedAndModified
            }
//            .let { Log.d(TAG, it.toString()); it }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6.from(
                    comparisonState = comparisonState,
                    operation = SyncOperation6.MUTUAL_RENAME_AND_COPY,
                    orderNum = n++
                ))
            }
        return n
    }

    companion object {
        val TAG: String = TwoPlaceItemMirrorProcessor::class.java.simpleName
    }
}


@AssistedFactory
interface TwoPlaceItemMirrorProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TwoPlaceItemMirrorProcessor
}