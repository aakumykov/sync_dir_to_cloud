package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.from
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notUnchangedOrDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class OnlyInTargetItemsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository6: SyncInstructionRepository6,
){
    /**
     * @return Порядковый номер для слежующего генератора инструкций.
     */
    suspend fun process(initialOrderNum: Int): Int {
        val nextOrderNum = processDirs(initialOrderNum)
        return processFiles(nextOrderNum)
    }


    /**
     * @return З
     */
    private suspend fun processDirs(initialOrderNum: Int): Int {
        var n = initialOrderNum
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter { it.isDir }
            .filter { it.notUnchangedOrDeletedInTarget }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6.from(
                    comparisonState = comparisonState,
                    operation = SyncOperation6.COPY_FROM_TARGET_TO_SOURCE,
                    orderNum = n++
                ))
            }
        return n
    }

    /**
     * @return Порядковый номер для следующего участника.
     */
    private suspend fun processFiles(initialOrderNum: Int): Int {
        var n = initialOrderNum
        comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter { it.isFile }
            .filter { it.notUnchangedOrDeletedInTarget }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6.from(
                    comparisonState = comparisonState,
                    operation = SyncOperation6.COPY_FROM_TARGET_TO_SOURCE,
                    orderNum = n++
                ))
            }
        return n
    }
}


@AssistedFactory
interface OnlyInTargetItemsProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): OnlyInTargetItemsProcessor
}