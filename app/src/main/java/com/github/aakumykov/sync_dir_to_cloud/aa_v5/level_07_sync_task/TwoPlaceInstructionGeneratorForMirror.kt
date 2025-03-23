package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceDeletedAndTargetModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceDeletedAndTargetNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceDeletedAndTargetUnchanged
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceModifiedAndTargetDeleted
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceModifiedAndTargetModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceModifiedAndTargetNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceModifiedAndTargetUnchanged
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceNewAndTargetDeleted
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceNewAndTargetModified
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

class TwoPlaceInstructionGeneratorForMirror @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository6: SyncInstructionRepository6,
) {
    suspend fun generate(initialOrderNum: Int): Int {
        var nextOrderNum = processMutuallyUnchangedOrDeleted(initialOrderNum)

        // TODO: назвать методы согдасно их задаче

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
                it.isSourceDeletedAndTargetNew ||
                it.isSourceDeletedAndTargetModified
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
                it.isSourceNewAndTargetDeleted ||
                it.isSourceModifiedAndTargetUnchanged ||
                it.isSourceModifiedAndTargetDeleted
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
                it.isSourceDeletedAndTargetUnchanged
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
                it.isSourceNewAndTargetModified ||
                it.isSourceModifiedAndTargetNew ||
                it.isSourceModifiedAndTargetModified
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
        val TAG: String = TwoPlaceInstructionGeneratorForMirror::class.java.simpleName
    }
}


@AssistedFactory
interface TwoPlaceInstructionGeneratorForMirrorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TwoPlaceInstructionGeneratorForMirror
}