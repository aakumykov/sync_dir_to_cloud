package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.BackupInstructionExecutor
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.BackupInstructionExecutorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger.DatabaseFileOperationLogger
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.runInCoroutineExtended
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll

class BackupInstructionsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val parentScope: CoroutineScope,
    fileOperationLogger: DatabaseFileOperationLogger,
    syncInstructionUpdater: SyncInstructionUpdater,
    syncObjectDBReader: SyncObjectDBReader,
    private val backupInstructionExecutorAssistedFactory: BackupInstructionExecutorAssistedFactory,
)
    : CommonFileInstructionsProcessor(
    fileOperationLogger, syncInstructionUpdater, syncObjectDBReader)
{
    suspend fun process(list: Iterable<SyncInstruction>) {
        processReal(list.filter { it.isBackup })
    }


    private suspend fun processReal(list: List<SyncInstruction>) {
        backupDirs(list.filter { it.isDir })
        backupFiles(list.filter { it.isFile })
    }


    private suspend fun backupDirs(list: List<SyncInstruction>) {
        list.map { instruction ->
            backupItem(instruction, R.string.LOG_ITEM_backing_up_dir)
        }.joinAll()
    }


    private suspend fun backupFiles(list: List<SyncInstruction>) {
        list.map { instruction ->
            backupItem(instruction, R.string.LOG_ITEM_backing_up_file)
        }.joinAll()
    }


    private suspend fun backupItem(instruction: SyncInstruction, @StringRes operationName: Int): Job {

        val logItemId = newRandomId
        val itemRelativePath = instruction.relativePath
        val jobId = newRandomId

        val logBaseInfo = DatabaseFileOperationLogger.LogBaseInfo(
            taskId = syncTask.id,
            executionId = executionId,
            logItemId = logItemId,
            operationName = operationName,
            firstItem = itemRelativePath,
            secondItem = null
        )

        return runInCoroutineExtended(
            scope = parentScope,
            onStart = { logStarted(logBaseInfo, jobId) },
            onFinish = { logFinished(logBaseInfo) },
            onCancel = { logCancelled(logBaseInfo, it) },
            onError = { logError(logBaseInfo, it) },
        ) {
            if (instruction.isBackupInSource) backupInstructionExecutor.backupInSource(instruction)
            else if (instruction.isBackupInTarget) backupInstructionExecutor.backupInSource(instruction)
            else throw IllegalStateException("'Backup' sync instruction is not 'source' or 'target' side: $instruction")
        }.also {
            markInstructionAsProcessed(instruction)
        }
    }

    private val backupInstructionExecutor: BackupInstructionExecutor by lazy {
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