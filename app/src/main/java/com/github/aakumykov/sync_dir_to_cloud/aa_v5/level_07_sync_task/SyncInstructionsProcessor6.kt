package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
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
    suspend fun processUnprocessedInstructions() {
        processInstructions(true)
    }

    suspend fun processCurrentInstructions() {
        processInstructions(false)
    }

    private suspend fun processInstructions(selectUnprocessed: Boolean) {

        val list = if (selectUnprocessed) getAllUnprocessedSyncInstructions() else getCurentSyncInstructions()

        deleteFiles(list)
        deleteDirs(list)

        processCollisionResolution(true, list)
        processCollisionResolution(false, list)

        processDirsNotDeletion(list)
        processFilesNotDeletion(list)
    }

    private suspend fun processCollisionResolution(isDir: Boolean, list: Iterable<SyncInstruction6>) {
        list
            .filter { if (isDir) it.isDir else it.isFile }
            .filter { it.isCollisionResolution }
            .forEach { syncInstruction ->
                syncInstructionExecutor.execute(syncInstruction)
            }
    }


    private suspend fun deleteFiles(list: Iterable<SyncInstruction6>) {
        list
            .filter { it.isFile }
            .filter { it.isDeletion }
            .forEach { syncInstruction ->
                syncInstructionExecutor.execute(syncInstruction)
            }
    }

    private suspend fun deleteDirs(list: Iterable<SyncInstruction6>) {
        list
            .filter { it.isDir }
            .filter { it.isDeletion }
            .forEach { syncInstruction ->
                syncInstructionExecutor.execute(syncInstruction)
            }
    }

    private suspend fun processDirsNotDeletion(list: Iterable<SyncInstruction6>) {
        list
            .filter { it.isDir }
            .filter { it.notDeletion }
            .forEach { instruction ->
                syncInstructionExecutor.execute(instruction)
            }
    }

    private suspend fun processFilesNotDeletion(list: Iterable<SyncInstruction6>) {
        list
            .filter { it.isFile }
            .filter { it.notDeletion }
            .forEach { instruction ->
                syncInstructionExecutor.execute(instruction)
            }
    }


    private suspend fun getCurentSyncInstructions(): Iterable<SyncInstruction6> {
        return syncInstructionRepository6
            .getAllFor(syncTask.id, executionId)
    }

    private suspend fun getAllUnprocessedSyncInstructions(): Iterable<SyncInstruction6> {
        return syncInstructionRepository6
            .getAllWithoutExecutionId(syncTask.id)
            .filter { !it.isProcessed }
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