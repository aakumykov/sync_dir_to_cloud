package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.task

import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskMetadataReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TaskBackupDirPreparer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val topLevelDirPrefix: String,
    private val taskBackupDirCreatorAssistedFactory: TaskBackupDirCreatorAssistedFactory,
    private val syncTaskUpdater: SyncTaskUpdater,
    private val appPreferences: AppPreferences,
) {
    suspend fun prepareTaskBackupDirs(syncSide: SyncSide) {
        when(syncTask.syncMode!!) {
            SyncMode.SYNC -> prepareTopLevelBackupDirInTarget()
            SyncMode.MIRROR -> {
                when(syncSide) {
                    SyncSide.SOURCE -> prepareTopLevelBackupDirInSource()
                    SyncSide.TARGET -> prepareTopLevelBackupDirInTarget()
                }
            }
        }
    }


    private suspend fun prepareTopLevelBackupDirInSource() {
        val dirName = taskBackupDirCreator.createBaseBackupDirInSource()
        syncTaskUpdater.setSourceBackupDirName(syncTask.id, dirName)
    }


    private suspend fun prepareTopLevelBackupDirInTarget() {
        val dirName = taskBackupDirCreator.createBaseBackupDirInTarget()
        syncTaskUpdater.setTargetBackupDirName(syncTask.id, dirName)
    }


    private val taskBackupDirCreator: TaskBackupDirCreator by lazy {
        taskBackupDirCreatorAssistedFactory.create(
            syncTask = syncTask,
            dirNamePrefixSupplier = { appPreferences.BACKUPS_TOP_DIR_PREFIX },
            dirNameSuffixSupplier = { randomUUID },
            appPreferences.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }
}


@AssistedFactory
interface TaskBackupDirPreparerAssistedFactory {
    fun create(syncTask: SyncTask, topLevelDirPrefix: String): TaskBackupDirPreparer
}