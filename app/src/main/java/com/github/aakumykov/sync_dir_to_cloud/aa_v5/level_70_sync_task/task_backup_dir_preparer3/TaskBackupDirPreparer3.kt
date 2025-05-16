package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.task_backup_dir_preparer3

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.backup_dir_preparer2.UniqueDirNameMaker
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.backup_dir_preparer2.UniqueDirNameMakerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.sourceBackupsDirPath
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TaskBackupDirPreparer3 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val appPreferences: AppPreferences,
    private val dirCreator5AssistedFactory: DirCreator5AssistedFactory,
    private val uniqueDirNameMakerAssistedFactory: UniqueDirNameMakerAssistedFactory,
){
    /**
     * @return Имя созданного каталога бекапов.
     */
    suspend fun prepareSourceBackupDir(): String {
        val dirName = backupDirNameFor(syncTask)
        dirCreator.createDirInSource(basePath = syncTask.sourcePath!!, dirName = dirName)
        return dirName
    }

    /**
     * @return Имя созданного каталога бекапов.
     */
    suspend fun prepareTargetBackupDir(): String {
        val dirName = backupDirNameFor(syncTask)
        dirCreator.createDirInTarget(basePath = syncTask.targetPath!!, dirName = dirName)
        return dirName
    }


    private fun backupDirNameFor(syncTask: SyncTask): String {
        return syncTask.sourceBackupDirName ?: uniqueDirNameMaker.getUniqueDirName()
    }


    private suspend fun createUniqueDir() {

    }

    private val dirCreator: DirCreator5 by lazy {
        dirCreator5AssistedFactory.create(syncTask)
    }

    private val uniqueDirNameMaker: UniqueDirNameMaker by lazy{
        uniqueDirNameMakerAssistedFactory.create(
            syncTask,
            dirNamePrefixSupplier = { appPreferences.BACKUPS_TOP_DIR_PREFIX },
            dirNameSuffixSupplier = { randomUUID },
            appPreferences.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }
}


@AssistedFactory
interface TaskBackupDirPreparer3AssistedFactory {
    fun create(syncTask: SyncTask): TaskBackupDirPreparer3
}