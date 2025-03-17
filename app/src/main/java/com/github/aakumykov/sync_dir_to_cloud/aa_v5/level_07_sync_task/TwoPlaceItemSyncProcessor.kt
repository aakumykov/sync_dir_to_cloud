package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notMutuallyUnchanged
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
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
    /**
     * @return Увеличенный порядковый номер
     */
    suspend fun processSyncing(initialOrderNum: Int): Int {
        val nextOrderNum = processNeedToBeCopiedToTarget(initialOrderNum)
        return processNeedToBeDeletedInTarget(nextOrderNum)
    }

    private suspend fun processNeedToBeCopiedToTarget(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getAllComparisonStatesFor(syncTask.id, executionId)
            .filter { it.isBilateral }
            .filter { !it.isDeletedInSource }
            .filter { it.notMutuallyUnchanged }
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
                        operation = SyncOperation6.COPY_FROM_SOURCE_TO_TARGET,
                        orderNum = n++
                    ))
                }
            }
        return n
    }

    private suspend fun processNeedToBeDeletedInTarget(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getAllComparisonStatesFor(syncTask.id, executionId)
            .filter { it.isBilateral }
            .filter { it.isDeletedInSource }
            .filter { it.notDeletedInTarget }
            .forEach { comparisonState ->
                syncInstructionRepository6.apply {
                    if (syncTask.withBackup) {
                        add(SyncInstruction6.from(
                            comparisonState = comparisonState,
                            operation = SyncOperation6.BACKUP_IN_TARGET,
                            orderNum = n++
                        ))
                        add(SyncInstruction6.from(
                            comparisonState = comparisonState,
                            operation = SyncOperation6.DELETE_IN_TARGET,
                            orderNum = n++
                        ))
                    }
                }
            }
        return n
    }


    private suspend fun getAllComparisonStatesFor(
        taskId: String,
        executionId: String
    ): Iterable<ComparisonState> = comparisonStateRepository
        .getAllFor(taskId, executionId)
}


@AssistedFactory
interface TwoPlaceItemSyncProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TwoPlaceItemSyncProcessor
}