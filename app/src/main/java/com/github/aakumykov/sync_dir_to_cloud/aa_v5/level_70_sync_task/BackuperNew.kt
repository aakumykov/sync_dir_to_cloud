package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreator2
import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreator2AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Named

class BackuperNew @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted("executionId") private val executionId: String,
    @Assisted("topLevelDirPrefix") private val topLevelDirPrefix: String,
    private val backupDirCreatorFactory: BackupDirCreatorAssistedFactory,
    private val backupDirCreator2AssistedFactory: BackupDirCreator2AssistedFactory,
    private val syncTaskUpdater: SyncTaskUpdater,
) {
    suspend fun prepareBackup() {
        when(syncTask.syncMode!!) {
            SyncMode.SYNC -> prepareTopLevelBackupDirInSource()
            SyncMode.MIRROR -> {
                prepareTopLevelBackupDirInSource()
                prepareTopLevelBackupDirInTarget()
            }
        }
    }

    private suspend fun prepareTopLevelBackupDirInSource() {
        val dirName = backupDirCreator.createBackupDirInSource(syncTask, topLevelDirPrefix)
        syncTaskUpdater.setSourceBackupDir(syncTask.id, dirName)
    }

    private suspend fun prepareTopLevelBackupDirInTarget() {
        val dirName = backupDirCreator.createBackupDirInTarget(syncTask, topLevelDirPrefix)
        syncTaskUpdater.setTargetBackupDir(syncTask.id, dirName)
    }


    private val backupDirCreator by lazy {
        backupDirCreatorFactory.create(
            BackupConfig.BACKUPS_TOP_DIR_PREFIX,
            syncTask
        )
    }

    private val backupDirCreator2: BackupDirCreator2 by lazy {
        backupDirCreator2AssistedFactory.create(
            syncTask = syncTask,
            dirNamePrefixSupplier = { BackupConfig.BACKUPS_TOP_DIR_PREFIX },
            dirNameSuffixSupplier = { randomUUID },
            BackupConfig.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }
}


@AssistedFactory
interface BackuperNewAssistedFactory {
    fun create(
        syncTask: SyncTask,
        @Assisted("executionId") executionId: String,
        @Assisted("topLevelDirPrefix") topLevelDirPrefix: String
    ): BackuperNew
}