package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2

import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.BackupInstructionExecutor2AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.base.BasicInstructionsProcessorAssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlin.collections.filter

class BackupInstructionsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val parentScope: CoroutineScope,
    private val backupInstructionExecutorAssistedFactory: BackupInstructionExecutor2AssistedFactory,
    private val basicInstructionsProcessorAssistedFactory: BasicInstructionsProcessorAssistedFactory,
) {
    suspend fun process(list: Iterable<SyncInstruction>) {
        processReal(list.filter { it.isBackup })
    }

    private suspend fun processReal(list: List<SyncInstruction>) {
        backupDirs(list.filter { it.isDir })
        backupFiles(list.filter { it.isFile })
    }

    private suspend fun backupDirs(list: List<SyncInstruction>) {
        list.forEach { instruction ->
            basicInstructionsProcessor.process(
                parentScope = parentScope,
                operationName = R.string.LOG_ITEM_backing_up_dir,
                firstItem = instruction.relativePath,
                secondItem = null
            ) {
                if (instruction.isBackupInSource) backupInstructionExecutor.backupInSource(instruction)
                else if (instruction.isBackupInTarget) backupInstructionExecutor.backupInSource(instruction)
                else throw IllegalStateException("'Backup' sync instruction is not 'source' or 'target' side: $instruction")
            }
        }
    }

    private suspend fun backupFiles(list: List<SyncInstruction>) {
        list.forEach { instruction ->
            basicInstructionsProcessor.process(
                parentScope = parentScope,
                operationName = R.string.LOG_ITEM_backing_up_file,
                firstItem = instruction.relativePath,
                secondItem = null
            ) {
                if (instruction.isBackupInSource) backupInstructionExecutor.backupInSource(instruction)
                else if (instruction.isBackupInTarget) backupInstructionExecutor.backupInSource(instruction)
                else throw IllegalStateException("'Backup' sync instruction is not 'source' or 'target' side: $instruction")
            }
        }
    }

    private val basicInstructionsProcessor by lazy {
        basicInstructionsProcessorAssistedFactory.create(syncTask.id, executionId)
    }

    private val backupInstructionExecutor by lazy {
        backupInstructionExecutorAssistedFactory.create(syncTask)
    }
}

@AssistedFactory
interface BackupInstructionsProcessorAssistedFactory {
    fun create(
        syncTask: SyncTask,
        executionId: String,
        parentScope: CoroutineScope,
    ): BackupInstructionsProcessor
}