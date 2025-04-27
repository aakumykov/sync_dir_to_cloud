package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class BackuperNew @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val backupDirCreatorFactory: BackupDirCreatorAssistedFactory,
    private val syncTaskUpdater: SyncTaskUpdater,
) {
    suspend fun prepareBackup() {
        when(syncTask.syncMode!!) {
            SyncMode.SYNC -> prepareBackupDirInSource()
            SyncMode.MIRROR -> {
                prepareBackupDirInSource()
                prepareBackupDirInTarget()
            }
        }
    }

    private suspend fun prepareBackupDirInSource() {
        val dirName = backupDirCreator.createBackupDirInSource(syncTask)
        syncTaskUpdater.setSourceBackupDir(syncTask.id, dirName)
    }

    private suspend fun prepareBackupDirInTarget() {
        val dirName = backupDirCreator.createBackupDirInTarget(syncTask)
        syncTaskUpdater.setTargetBackupDir(syncTask.id, dirName)
    }


    private val backupDirCreator by lazy {
        backupDirCreatorFactory.create(syncTask)
    }
}


@AssistedFactory
interface BackuperNewAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): BackuperNew
}