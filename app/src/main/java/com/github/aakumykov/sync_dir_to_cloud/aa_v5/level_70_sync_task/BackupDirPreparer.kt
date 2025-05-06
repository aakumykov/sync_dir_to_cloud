package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreator
import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.extensions.lastStartTime
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskMetadataReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import com.github.aakumykov.sync_dir_to_cloud.utils.formattedDateTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class BackupDirPreparer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted("executionId") private val executionId: String,
    @Assisted("topLevelDirPrefix") private val topLevelDirPrefix: String,
    private val backupDirCreatorAssistedFactory: BackupDirCreatorAssistedFactory,
    private val syncTaskUpdater: SyncTaskUpdater,
    private val appPreferences: AppPreferences,
    private val taskMetadataReader: SyncTaskMetadataReader,
) {
    suspend fun prepareTaskBackupDirs() {
        when(syncTask.syncMode!!) {
            SyncMode.SYNC -> prepareTopLevelBackupDirInTarget()
            SyncMode.MIRROR -> {
                prepareTopLevelBackupDirInSource()
                prepareTopLevelBackupDirInTarget()
            }
        }
    }


    /**
     * Создаёт каталог для бекапов текущего выполнения в источнике.
     */
    suspend fun createExecutionBackupDirInSource(): String {
        return executionBackupDirCreator.createBaseBackupDirInSource()
    }


    /**
     * Создаёт каталог для бекапов текущего выполнения в приёмнике.
     */
    suspend fun createExecutionBackupDirInTarget(): String {
        return executionBackupDirCreator.createBaseBackupDirInSource()
    }


    private suspend fun prepareTopLevelBackupDirInSource() {
        val dirName = taskBackupDirCreator.createBaseBackupDirInSource()
        syncTaskUpdater.setSourceBackupDir(syncTask.id, dirName)
    }


    private suspend fun prepareTopLevelBackupDirInTarget() {
        val dirName = taskBackupDirCreator.createBaseBackupDirInTarget()
        syncTaskUpdater.setTargetBackupDir(syncTask.id, dirName)
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
            dirNameSuffixSupplier = { "${formattedDateTime(syncTask.lastStartTime)}${executionBackupDirAppendix}" },
            appPreferences.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }

    private var _executionBackupDirAppendix: Int? = null
    private val executionBackupDirAppendix: String
        get() = _executionBackupDirAppendix?.let { "_${it}" } ?: ""
}


@AssistedFactory
interface BackupDirPreparerAssistedFactory {
    fun create(
        syncTask: SyncTask,
        @Assisted("executionId") executionId: String,
        @Assisted("topLevelDirPrefix") topLevelDirPrefix: String
    ): BackupDirPreparer
}