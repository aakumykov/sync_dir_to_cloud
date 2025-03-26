package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.repository.ComparisonStateRepository

open class BasicInstructionGenerator(
    private val taskId: String,
    private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository: SyncInstructionRepository6,
) {
    suspend fun getStatesForThisTaskAndExecution(): Iterable<ComparisonState> {
        return comparisonStateRepository
            .getAllFor(taskId, executionId)
    }


    suspend fun generateSyncInstructionsFrom(
        comparisonStateList: Iterable<ComparisonState>,
        syncOperation6: SyncOperation6,
        nextOrderNum: Int
    ): Int {
        var n = nextOrderNum
        syncInstructionRepository.apply {
            comparisonStateList.forEach { comparisonState ->
                add(
                    SyncInstruction6.from(
                        comparisonState = comparisonState,
                        operation = syncOperation6,
                        orderNum = n++
                    )
                )
            }
        }
        return n
    }
}