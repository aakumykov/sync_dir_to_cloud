package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreator
import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class BackupDirPreparer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted("executionId") private val executionId: String,
    @Assisted("topLevelDirPrefix") private val topLevelDirPrefix: String,
    private val backupDirCreatorAssistedFactory: BackupDirCreatorAssistedFactory,
    private val syncTaskUpdater: SyncTaskUpdater,
) {
    suspend fun prepareBackupDirs() {
        when(syncTask.syncMode!!) {
            SyncMode.SYNC -> prepareTopLevelBackupDirInTarget()
            SyncMode.MIRROR -> {
                prepareTopLevelBackupDirInSource()
                prepareTopLevelBackupDirInTarget()
            }
        }
    }

    private suspend fun prepareTopLevelBackupDirInSource() {
        val dirName =
//            backupDirCreator.createBackupDirInSource(syncTask, topLevelDirPrefix)
            backupDirCreator.createBaseBackupDirInSource()
        syncTaskUpdater.setSourceBackupDir(syncTask.id, dirName)
    }


    private suspend fun prepareTopLevelBackupDirInTarget() {
        val dirName =
//            backupDirCreator.createBackupDirInTarget(syncTask, topLevelDirPrefix)
            backupDirCreator.createBaseBackupDirInTarget()
        syncTaskUpdater.setTargetBackupDir(syncTask.id, dirName)
    }


    private val backupDirCreator: BackupDirCreator by lazy {
        backupDirCreatorAssistedFactory.create(
            syncTask = syncTask,
            dirNamePrefixSupplier = { BackupConfig.BACKUPS_TOP_DIR_PREFIX },
            dirNameSuffixSupplier = { randomUUID },
            BackupConfig.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }
}


@AssistedFactory
interface BackupDirPreparerAssistedFactory {
    fun create(
        syncTask: SyncTask,
        @Assisted("executionId") executionId: String,
        @Assisted("topLevelDirPrefix") topLevelDirPrefix: String
    ): BackupDirPreparer
}