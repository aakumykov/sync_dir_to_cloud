package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreator
import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskMetadataReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncTaskBackupDirPreparer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val topLevelDirPrefix: String,
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
}


@AssistedFactory
interface SyncTaskBackupDirPreparerAssistedFactory {
    fun create(syncTask: SyncTask, topLevelDirPrefix: String): SyncTaskBackupDirPreparer
}