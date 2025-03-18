package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notUnchangedOrDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class OnlyInSourceInstructionGenerator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository6: SyncInstructionRepository6,
){
    suspend fun generate(initialOrderNum: Int): Int {
        val nextOrderNum = processNotDeletedDirs(initialOrderNum)
        return processNotDeletedFiles(nextOrderNum)
    }

    private suspend fun processNotDeletedDirs(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getOnlyInSourceStates()
            .let { it }
            .filter { it.onlySource }
            .let { it }
            .filter { it.isDir }
            .let { it }
            .filter { it.notDeletedInSource }
            .let { it }
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

    private suspend fun processNotDeletedFiles(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getOnlyInSourceStates()
            .let { it }
            .filter { it.onlySource }
            .let { it }
            .filter { it.isFile }
            .let { it }
            .filter { it.notDeletedInSource }
            .let { it }
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

    private suspend fun getOnlyInSourceStates(): Iterable<ComparisonState> {
        return comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter {
                null != it.sourceObjectState &&
                        null == it.targetObjectState
            }
    }
}



@AssistedFactory
interface OnlyInSourceInstructionGeneratorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): OnlyInSourceInstructionGenerator
}