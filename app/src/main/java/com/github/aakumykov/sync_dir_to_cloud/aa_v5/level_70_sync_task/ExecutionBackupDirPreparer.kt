package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreator
import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskMetadataReader
import com.github.aakumykov.sync_dir_to_cloud.utils.formattedDateTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ExecutionBackupDirPreparer @AssistedInject constructor(
    @Assisted private val syncSide: SyncSide,
    @Assisted private val syncTask: SyncTask,
    private val backupDirCreatorAssistedFactory: BackupDirCreatorAssistedFactory,
    private val taskMetadataReader: SyncTaskMetadataReader,
    private val appPreferences: AppPreferences,
) {
    private var generationCounter: Int = 0


    suspend fun createExecutionBackupDir(): String {
        return when (syncSide) {
            SyncSide.SOURCE -> createExecutionBackupDirInSource()
            SyncSide.TARGET -> createExecutionBackupDirInTarget()
        }
    }


    private suspend fun createExecutionBackupDirInSource(): String {
        return executionBackupDirCreator.createBaseBackupDirInSource()
    }

    private suspend fun createExecutionBackupDirInTarget(): String {
        return executionBackupDirCreator.createBaseBackupDirInTarget()
    }


    private val executionBackupDirCreator: BackupDirCreator by lazy {
        backupDirCreatorAssistedFactory.create(
            syncTask = syncTask,
            dirNamePrefixSupplier = { appPreferences.BACKUPS_DIR_PREFIX },
            dirNameSuffixSupplier = { dirNameSuffix },
            appPreferences.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }


    private val dirNameSuffix: String
        get() = "${formattedDateTime(0L)}${createExecutionBackupDirAppendix()}"


    private fun createExecutionBackupDirAppendix(): String {
        val appendix = if (0 == generationCounter) { "" } else { "_${generationCounter}" }
        generationCounter++
        return appendix
    }


    private val taskStartTime: Long
        get() = taskMetadataReader.getStartingTime(syncTask.id) ?: throw IllegalStateException("SyncTask has no info about its starting time")

}


@AssistedFactory
interface ExecutionBackupDirPreparerAssistedFactory {
    fun create(
        syncTask: SyncTask,
        syncSide: SyncSide,
    ): ExecutionBackupDirPreparer
}