package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_08_instructions.generator

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.ComparisonStateRepository

open class BasicInstructionGenerator(
    private val taskId: String,
    private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository: SyncInstructionRepository,
) {
    suspend fun getStatesForThisTaskAndExecution(): Iterable<ComparisonState> {
        return comparisonStateRepository
            .getAllFor(taskId, executionId)
    }


    suspend fun generateSyncInstructionsFrom(
        comparisonStateList: Iterable<ComparisonState>,
        syncOperation: SyncOperation,
        nextOrderNum: Int
    ): Int {
        var n = nextOrderNum
        syncInstructionRepository.apply {
            comparisonStateList.forEach { comparisonState ->
                add(
                    SyncInstruction.from(
                        comparisonState = comparisonState,
                        operation = syncOperation,
                        orderNum = n++
                    )
                )
            }
        }
        return n
    }
}