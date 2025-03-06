package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notUnchangedOrDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notUnchangedOrDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class OnlyInSourceItemsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository6: SyncInstructionRepository6,
){
    //Несмотря на то, что инструкция к фалам и каталогам применяется одна,
    // вначале должны быть созданы каталоги.
    suspend fun process() {
        processUnchangedNewModifiedDirs()
        processUnchangedNewModifiedFiles()
    }

    private suspend fun processUnchangedNewModifiedDirs() {
        getOnlyInSourceStates()
            .filter { it.isDir }
            .let { it }
            .filter { it.notUnchangedOrDeletedInSource }
            .let { it }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6(
                    id = randomUUID,
                    taskId = syncTask.id,
                    executionId = executionId,
                    objectIdInSource = comparisonState.sourceObjectId,
                    objectIdInTarget = comparisonState.targetObjectId,
                    operation = SyncOperation6.COPY_FROM_SOURCE_TO_TARGET,
                    relativePath = comparisonState.relativePath,
                ))
            }
    }

    private suspend fun processUnchangedNewModifiedFiles() {
        getOnlyInSourceStates()
            .filter { it.isFile }
            .let { it }
            .filter { it.notUnchangedOrDeletedInSource }
            .let { it }
            .forEach { comparisonState ->
                syncInstructionRepository6.add(SyncInstruction6(
                    id = randomUUID,
                    taskId = syncTask.id,
                    executionId = executionId,
                    objectIdInSource = comparisonState.sourceObjectId,
                    objectIdInTarget = comparisonState.targetObjectId,
                    operation = SyncOperation6.COPY_FROM_SOURCE_TO_TARGET,
                    relativePath = comparisonState.relativePath,
                ))
            }
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
interface OnlyInSourceItemsProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): OnlyInSourceItemsProcessor
}