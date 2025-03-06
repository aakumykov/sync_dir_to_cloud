package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.from
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notUnchangedOrDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class OnlyInSourceInstructionCreator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository6: SyncInstructionRepository6,
){
    //Несмотря на то, что инструкция к фалам и каталогам применяется одна,
    // вначале должны быть созданы каталоги.
    suspend fun process(initialOrderNum: Int): Int {
        val nextOrderNum = processUnchangedNewModifiedDirs(initialOrderNum)
        return processUnchangedNewModifiedFiles(nextOrderNum)
    }

    private suspend fun processUnchangedNewModifiedDirs(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getOnlyInSourceStates()
            .filter { it.onlySource }
            .filter { it.isDir }
            .filter { it.notUnchangedOrDeletedInSource }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6.from(
                    comparisonState = comparisonState,
                    operation = SyncOperation6.COPY_FROM_SOURCE_TO_TARGET,
                    orderNum = n++
                ))
            }
        return n
    }

    private suspend fun processUnchangedNewModifiedFiles(initialOrderNum: Int): Int {
        var n = initialOrderNum
        getOnlyInSourceStates()
            .filter { it.onlySource }
            .filter { it.isFile }
            .filter { it.notUnchangedOrDeletedInSource }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6.from(
                    comparisonState = comparisonState,
                    operation = SyncOperation6.COPY_FROM_SOURCE_TO_TARGET,
                    orderNum = n++
                ))
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
interface OnlyInSourceInstructionCreatorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): OnlyInSourceInstructionCreator
}