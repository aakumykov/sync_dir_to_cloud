package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_91_instructions

import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.BackupInstructionExecutor2AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.FileInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.app_settings.AppSettings
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.extensions.notProcessed
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2
import com.github.aakumykov.sync_dir_to_cloud.repository.FileOperationLogRepository2
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import com.github.aakumykov.sync_dir_to_cloud.utils.CritNonCritExecutor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlin.collections.filter
import kotlin.math.log

class Backuper2() {

}

class FileInstructionsProcessor2 @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    @Assisted private val syncTask: SyncTask,
    @Assisted private val scope: CoroutineScope,
    private val appSettings: AppSettings,
    private val syncInstructionRepository: SyncInstructionRepository,
    private val logger: FileOperationLogger2,
    private val backupInstructionExecutor2AssistedFactory: BackupInstructionExecutor2AssistedFactory,
) {
    private val critNonCritExecutor by lazy { CritNonCritExecutor() }

    suspend fun processFileInstructions(unprocessed: Boolean) {

        // Как бекапить файлы в каталоге, который тоже предстоить бекапить?
//        prepareBackupDirs(list)
//        backupFilesAndDirs(list(unprocessed))
        backupFilesAndDirs2(list(unprocessed))

        deleteFiles(list(unprocessed))
        deleteDirs(list(unprocessed))

        processCollisionResolution(true, list(unprocessed))
        processCollisionResolution(false, list(unprocessed))

        processDirsCreation(list(unprocessed))
        processFilesCopying(list(unprocessed))
    }

    private suspend fun backupFilesAndDirs2(list: Iterable<SyncInstruction>) {
        list
            .filter { it.isBackup }
            .forEach { syncInstruction ->
                critNonCritExecutor.execute(
                    scope = scope,
                    isCriticalSupplier = { appSettings.backupIsCriticalOperation },
                    onStart = {
                        logger.logStarted()
                    },
                    onFinish = {
                        logger.logFinished()
                    },
                    onNonCriticalError = { t ->
                        logger.logError()
                    },
                    onCriticalError = { t ->
                        logger.logError()
                    },
                    onCancelled = { e ->
                        logger.logCancelled()
                    }
                ) {
                    backuper2.processBackup(syncInstruction)
                }
            }
    }


    /*private suspend fun prepareBackupDirs(list: Iterable<SyncInstruction>) {
        if (list.hasSourceBackups) backupDirsPreparer.prepareBackupDirs()
        if (list.hasTargetBackups) backupDirsPreparer.prepareBackupDirs()
    }*/


    //  TODO: в источнике/приёмнике
    private suspend fun backupFilesAndDirs(list: Iterable<SyncInstruction>) {
        list
            .filter { it.isBackup }
            .apply {
                // Сначала бекапятся (создаются) каталоги, потом файлы.
                filter { it.isDir }
                    .forEach { syncInstruction ->
                        oneSyncInstructionExecutor.execute(syncInstruction)
                    }

                filter { it.isFile }
                    .forEach { syncInstruction ->
                        oneSyncInstructionExecutor.execute(syncInstruction)
                    }
            }

    }


    private suspend fun processCollisionResolution(isDir: Boolean, list: Iterable<SyncInstruction>) {
        list
            .filter { if (isDir) it.isDir else it.isFile }
            .filter { it.isCollisionResolution }
            .forEach { syncInstruction ->
                oneSyncInstructionExecutor.execute(syncInstruction)
            }
    }


    private suspend fun deleteFiles(list: Iterable<SyncInstruction>) {
        list
            .filter { it.isFile }
            .filter { it.isDeletion }
            .forEach { syncInstruction ->
                oneSyncInstructionExecutor.execute(syncInstruction)
            }
    }

    private suspend fun deleteDirs(list: Iterable<SyncInstruction>) {
        list
            .filter { it.isDir }
            .filter { it.isDeletion }
            .sortedBy { it.relativePath.length }
            .reversed()
            .forEach { syncInstruction ->
                oneSyncInstructionExecutor.execute(syncInstruction)
            }
    }

    private suspend fun processDirsCreation(list: Iterable<SyncInstruction>) {
        list
            .filter { it.isDir }
            .filter { it.notDeletion }
            .forEach { instruction ->
                oneSyncInstructionExecutor.execute(instruction)
            }
    }

    private suspend fun processFilesCopying(list: Iterable<SyncInstruction>) {
        list
            .filter { it.isFile }
            .filter { it.isCopying }
            .forEach { instruction ->
                oneSyncInstructionExecutor.execute(instruction)
            }
    }


    private suspend fun getNonProcessedSyncInstructionsForTaskAndExecution(): Iterable<SyncInstruction> {
        return syncInstructionRepository
            .getAllFor(taskId, executionId)
            .filter { it.notProcessed }
    }

    private suspend fun getNonProcessedInstructionsForTask(): Iterable<SyncInstruction> {
        return syncInstructionRepository
            .getAllWithoutExecutionId(taskId)
            .filter { it.notProcessed }
    }


    private suspend fun list(selectUnprocessed: Boolean): Iterable<SyncInstruction> {
        return if (selectUnprocessed) getNonProcessedInstructionsForTask()
        else getNonProcessedSyncInstructionsForTaskAndExecution()
    }


    private val backupInstructionExecutor by lazy {
        backupInstructionExecutor2AssistedFactory.create(syncTask)
    }

    companion object {
        val TAG: String = FileInstructionsProcessor2::class.java.simpleName
    }
}

@AssistedFactory
interface FileInstructionsProcessor2AssistedFactory {
    fun create(
        taskId: String,
        executionId: String,
        syncTask: SyncTask,
        scope: CoroutineScope,
    ): FileInstructionsProcessor2
}