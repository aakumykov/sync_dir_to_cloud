package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository6
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope


class SyncInstructionsProcessor6 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val scope: CoroutineScope,
    private val syncOptions: SyncOptions,
    private val syncInstructionRepository6: SyncInstructionRepository6,
    private val syncInstructionExecutorAssistedFactory: SyncInstructionExecutorAssistedFactory,
) {
    suspend fun processInstructions() {
        deleteFiles()
        deleteDirs()

        processDirsNotDeletion()
        processFilesNotDeletion()
    }

    private suspend fun deleteFiles() {
        syncInstructionRepository6
            .getAllFor(syncTask.id, executionId)
            .filter { it.isFile }
            .filter { it.isDeletion }
            .forEach { syncInstruction ->
                syncInstructionExecutor.execute(syncInstruction)
            }
    }

    private suspend fun deleteDirs() {
        syncInstructionRepository6
            .getAllFor(syncTask.id, executionId)
            .filter { it.isDir }
            .filter { it.isDeletion }
            .forEach { syncInstruction ->
                syncInstructionExecutor.execute(syncInstruction)
            }
    }

    private suspend fun processDirsNotDeletion() {
        syncInstructionRepository6
            .getAllFor(syncTask.id, executionId)
            .filter { it.isDir }
            .filter { it.notDeletion }
            .forEach { instruction ->
                syncInstructionExecutor.execute(instruction)
            }
    }

    private suspend fun processFilesNotDeletion() {
        syncInstructionRepository6
            .getAllFor(syncTask.id, executionId)
            .let { it }
            .filter { it.isFile }
            .let { it }
            .filter { it.notDeletion }
            .let { it }
            .forEach { instruction ->
                syncInstructionExecutor.execute(instruction)
            }
    }

    private val syncInstructionExecutor by lazy {
        syncInstructionExecutorAssistedFactory.create(syncTask, executionId, scope)
    }
}


@AssistedFactory
interface SyncInstructionsProcessorAssistedFactory6 {
    fun create(
        syncTask: SyncTask,
        executionId: String,
        scope: CoroutineScope,
    ): SyncInstructionsProcessor6
}