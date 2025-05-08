package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.execution.ExecutionBackupDirPreparerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.task.TaskBackupDirPreparerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskMetadataReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class BackupDirsPreparer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val taskMetadataReader: SyncTaskMetadataReader,
    private val executionBackupDirPreparerAssistedFactory: ExecutionBackupDirPreparerAssistedFactory,
    private val taskBackupDirPreparerAssistedFactory: TaskBackupDirPreparerAssistedFactory,
    private val appPreferences: AppPreferences,
) {
    suspend fun prepareBackupDirs() {
        if (syncTask.withBackup) {

            taskBackupDirPreparerAssistedFactory.create(
                syncTask,
                appPreferences.BACKUPS_TOP_DIR_PREFIX
            ).prepareTaskBackupDirs()

            executionBackupDirPreparerAssistedFactory
                .create(syncTask)
                .prepareExecutionBackupDirs()
        }
    }
}


@AssistedFactory
interface BackupDirsPreparerAssistedFactory {
    fun create(syncTask: SyncTask): BackupDirsPreparer
}