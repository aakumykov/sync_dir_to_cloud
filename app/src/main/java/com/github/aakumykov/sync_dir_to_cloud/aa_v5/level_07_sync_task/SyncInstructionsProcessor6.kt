package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope


class SyncInstructionsProcessor6 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val scope: CoroutineScope,
    private val syncOptions: SyncOptions,
    private val syncInstructionRepository: SyncInstructionRepository,
    private val syncInstructionExecutorAssistedFactory: SyncInstructionExecutorAssistedFactory,
) {
    suspend fun processUnprocessedInstructions() {
        processInstructions(true)
    }

    suspend fun processCurrentInstructions() {
        processInstructions(false)
    }

    private suspend fun processInstructions(selectUnprocessed: Boolean) {

        val list = if (selectUnprocessed) getAllUnprocessedSyncInstructions()
                    else getCurentSyncInstructions()

        // Как бекапить файлы в каталоге, который тоже предстоить бекапить?
        backupFilesAndDirs(list)

        deleteFiles(list)
        deleteDirs(list)

        processCollisionResolution(true, list)
        processCollisionResolution(false, list)

        processDirsDirsCreation(list)
        processFilesCopying(list)
    }

    //  TODO: в источнике/приёмнике
    private fun backupFilesAndDirs(list: Iterable<SyncInstruction>) {
        list.filter { it.isBackup }
        backupDirs(list.filter { it.isDir })
        backupFiles(list.filter { it.isFile })
    }

    private fun backupDirs(dirInstructionsList: List<SyncInstruction>) {
        Log.d(TAG, dirInstructionsList.toString())
    }

    private fun backupFiles(fileInstructionsList: List<SyncInstruction>) {
        Log.d(TAG, fileInstructionsList.toString())
    }

    private suspend fun processCollisionResolution(isDir: Boolean, list: Iterable<SyncInstruction>) {
        list
            .filter { if (isDir) it.isDir else it.isFile }
            .filter { it.isCollisionResolution }
            .forEach { syncInstruction ->
                syncInstructionExecutor.execute(syncInstruction)
            }
    }


    private suspend fun deleteFiles(list: Iterable<SyncInstruction>) {
        list
            .filter { it.isFile }
            .filter { it.isDeletion }
            .forEach { syncInstruction ->
                syncInstructionExecutor.execute(syncInstruction)
            }
    }

    private suspend fun deleteDirs(list: Iterable<SyncInstruction>) {
        list
            .filter { it.isDir }
            .filter { it.isDeletion }
            .forEach { syncInstruction ->
                syncInstructionExecutor.execute(syncInstruction)
            }
    }

    private suspend fun processDirsDirsCreation(list: Iterable<SyncInstruction>) {
        list
            .filter { it.isDir }
            .filter { it.notDeletion }
            .forEach { instruction ->
                syncInstructionExecutor.execute(instruction)
            }
    }

    private suspend fun processFilesCopying(list: Iterable<SyncInstruction>) {
        list
            .filter { it.isFile }
            .filter { it.isCopying }
            .let { it }
            .forEach { instruction ->
                syncInstructionExecutor.execute(instruction)
            }
    }


    private suspend fun getCurentSyncInstructions(): Iterable<SyncInstruction> {
        return syncInstructionRepository
            .getAllFor(syncTask.id, executionId)
    }

    private suspend fun getAllUnprocessedSyncInstructions(): Iterable<SyncInstruction> {
        return syncInstructionRepository
            .getAllWithoutExecutionId(syncTask.id)
            .filter { !it.isProcessed }
    }


    private val syncInstructionExecutor by lazy {
        syncInstructionExecutorAssistedFactory.create(syncTask, executionId, scope)
    }

    companion object {
        val TAG: String = SyncInstructionsProcessor6::class.java.simpleName
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