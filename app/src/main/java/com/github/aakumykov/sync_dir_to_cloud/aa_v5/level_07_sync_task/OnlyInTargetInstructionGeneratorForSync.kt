package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notUnchangedOrDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class OnlyInTargetInstructionGeneratorForSync @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository6: SyncInstructionRepository6,
){
    /**
     * @return Порядковый номер для слежующего генератора инструкций.
     */
    suspend fun generate(initialOrderNum: Int): Int {
        val nextOrderNum = processDirsNeedsToBeDeleted(initialOrderNum)
        return processFilesNeedsToBeDeleted(nextOrderNum)
    }


    /**
     * @return
     */
    private suspend fun processDirsNeedsToBeDeleted(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getAllStatesFor(syncTask.id, executionId)
            .let { it }
            .filter { it.onlyTarget }
            .filter { it.isDir }
            .filter { it.notUnchangedOrDeletedInTarget }
            .let { it }
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
    private suspend fun processFilesNeedsToBeDeleted(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getAllStatesFor(syncTask.id, executionId)
            .let { it }
            .filter { it.onlyTarget }
            .filter { it.isFile }
            .filter { it.notUnchangedOrDeletedInTarget }
            .let { it }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6.from(
                    comparisonState = comparisonState,
                    operation = SyncOperation6.COPY_FROM_TARGET_TO_SOURCE,
                    orderNum = n++
                ))
            }
        return n
    }

    private suspend fun getAllStatesFor(taskId: String, executionId: String): Iterable<ComparisonState> {
        return comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
    }
}


@AssistedFactory
interface OnlyInTargetInstructionGeneratorForSyncAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): OnlyInTargetInstructionGeneratorForSync
}