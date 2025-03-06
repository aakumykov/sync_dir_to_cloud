package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.from
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notUnchangedInBothPlaces
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
    suspend fun process(initialOrderNum: Int): Int {
        val nextOrderNum = processNeedToBeCopiedToTarget(initialOrderNum)
        return processNeedToBeDeletedInTarget(nextOrderNum)
    }

    private suspend fun processNeedToBeCopiedToTarget(initialOrderNum: Int): Int {
        var n = initialOrderNum
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter { it.isBilateral }
            .filter { it.notDeletedInSource }
            .filter { it.notUnchangedInBothPlaces }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6.from(
                    comparisonState = comparisonState,
                    operation = SyncOperation6.COPY_FROM_SOURCE_TO_TARGET,
                    orderNum = n++
                ))
            }
        return n
    }

    private suspend fun processNeedToBeDeletedInTarget(initialOrderNum: Int): Int {
        var n = initialOrderNum
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter { it.isBilateral }
            .filter { it.isDeletedInSource }
            .filter { it.notDeletedInTarget }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6.from(
                    comparisonState = comparisonState,
                    operation = SyncOperation6.DELETE_IN_TARGET,
                    orderNum = n++
                ))
            }
        return n
    }
}


@AssistedFactory
interface TwoPlaceItemSyncProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TwoPlaceItemSyncProcessor
}