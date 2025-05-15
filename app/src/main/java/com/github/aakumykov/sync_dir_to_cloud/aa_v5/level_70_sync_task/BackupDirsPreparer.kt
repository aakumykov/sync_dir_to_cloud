package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.BackupDirCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.BackupDirCreatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import com.github.aakumykov.sync_dir_to_cloud.utils.backupDirFormattedDateTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class BackupDirsPreparer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val syncInstructionReader: SyncInstructionReader,

    private val backupDirCreatorAssistedFactory: BackupDirCreatorAssistedFactory,

    private val appPreferences: AppPreferences,
    private val syncTaskUpdater: SyncTaskUpdater,
) {
    //
    // Если есть, что бэкапить в источнике, создаю каталоги бекапа в источнике.
    // Если есть, что бэкапить в приёмнике, создаю каталоги бекапа в приёмнике.
    //
    suspend fun prepareBackupDirs() {
        if (hasBackupsInSource()) createBackupDirsInSource()
        if (hasBackupsInTarget()) createBackupDirsInTarget()
    }


    private fun hasBackupsInSource(): Boolean {
        return null != syncInstructionList.firstOrNull { it.isBackupInSource }.let { it }
    }


    private fun hasBackupsInTarget(): Boolean {
        return null != syncInstructionList.firstOrNull { it.isBackupInTarget }.let { it }
    }


    private suspend fun createBackupDirsInSource() {
        taskBackupDirCreator.createBackupDirIn(SyncSide.SOURCE, syncTask.sourcePath!!)
            .also { dirName ->
                syncTaskUpdater.setSourceBackupDirName(syncTask.id, dirName)
            }.also {
                executionBackupDirCreator.createBackupDirIn(
                    SyncSide.SOURCE,
                    combineFSPaths(syncTask.sourcePath!!, it)
                ).also { dirName ->
                    syncTaskUpdater.setSourceExecutionBackupDirName(syncTask.id, dirName)
                }
            }
    }


    private suspend fun createBackupDirsInTarget() {

        taskBackupDirCreator.createBackupDirIn(SyncSide.TARGET, syncTask.targetPath!!).also { dirName ->
            syncTaskUpdater.setTargetBackupDirName(syncTask.id, dirName)
        }

        executionBackupDirCreator.createBackupDirInTarget().also { dirName ->
            syncTaskUpdater.setTargetExecutionBackupDirName(syncTask.id, dirName)
        }
    }


    private val syncInstructionList: List<SyncInstruction> by lazy {
        syncInstructionReader.getSyncInstructionsFor(syncTask.id)
    }


    private val taskBackupDirCreator: BackupDirCreator by lazy {
        backupDirCreatorAssistedFactory.create(
            syncTask = syncTask,
            dirNamePrefixSupplier = { appPreferences.BACKUPS_TOP_DIR_PREFIX },
            dirNameSuffixSupplier = { randomUUID },
            appPreferences.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }

    private val executionBackupDirCreator: BackupDirCreator by lazy {
        backupDirCreatorAssistedFactory.create(
            syncTask = syncTask,
            dirNamePrefixSupplier = { appPreferences.BACKUPS_DIR_PREFIX },
            dirNameSuffixSupplier = { executionDirNameSuffix },
            appPreferences.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }

    // FIXME: вот это здесь ни к чему
    private val executionDirNameSuffix: String
        get() = "${backupDirFormattedDateTime(taskStartTime)}${createExecutionBackupDirAppendix()}"


    private fun createExecutionBackupDirAppendix(): String {
        val appendix = if (0 == generationCounter) { "" } else { "_${generationCounter}" }
        generationCounter++
        return appendix
    }


    private val taskStartTime: Long
        get() = syncTask.lastStart ?: throw IllegalStateException("SyncTask has no info about its starting time")


    // Это поле является "состоянием" объекта.
    // Бесконфликтная работа обеспечена тем, что его значение меняется
    // строго последовательно внутри этого класса.
    private var generationCounter: Int = 0
}


@AssistedFactory
interface BackupDirsPreparerAssistedFactory {
    fun create(syncTask: SyncTask): BackupDirsPreparer
}