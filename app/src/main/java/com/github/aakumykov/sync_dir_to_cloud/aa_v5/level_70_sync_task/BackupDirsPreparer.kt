package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.execution.ExecutionBackupDirPreparerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.task.TaskBackupDirPreparerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.executionBackupDirNameFor
import com.github.aakumykov.sync_dir_to_cloud.extensions.taskBackupDirNameFor
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskMetadataReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class BackupDirsPreparer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val taskBackupDirPreparerAssistedFactory: TaskBackupDirPreparerAssistedFactory,
    private val executionBackupDirPreparerAssistedFactory: ExecutionBackupDirPreparerAssistedFactory,
    private val appPreferences: AppPreferences,
) {
    suspend fun prepareBackupDirs(syncSide: SyncSide) {
        if (syncTask.withBackup) {

            if (null == syncTask.taskBackupDirNameFor(syncSide)) {
                taskBackupDirPreparerAssistedFactory
                    .create(syncTask, appPreferences.BACKUPS_TOP_DIR_PREFIX)
                    .prepareTaskBackupDirs(syncSide)
            }

            if (null == syncTask.executionBackupDirNameFor(syncSide)) {
                executionBackupDirPreparerAssistedFactory
                    .create(syncTask)
                    .prepareExecutionBackupDirs(syncSide)
            }
        }
    }
}


@AssistedFactory
interface BackupDirsPreparerAssistedFactory {
    fun create(syncTask: SyncTask): BackupDirsPreparer
}