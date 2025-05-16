package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TaskBackupDirPreparer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val appPreferences: AppPreferences,
    private val dirCreator5AssistedFactory: DirCreator5AssistedFactory,
    private val taskUniqueDirNameMakerAssistedFactory: TaskUniqueDirNameMakerAssistedFactory,
){
    /**
     * @return Имя созданного каталога бекапов.
     */
    suspend fun prepareSourceBackupDir(): String {
        val dirName = sourceBackupDirNameFor(syncTask)
        dirCreator.createDirInSource(basePath = syncTask.sourcePath!!, dirName = dirName)
        return dirName
    }

    /**
     * @return Имя созданного каталога бекапов.
     */
    suspend fun prepareTargetBackupDir(): String {
        val dirName = targetBackupDirNameFor(syncTask)
        dirCreator.createDirInTarget(basePath = syncTask.targetPath!!, dirName = dirName)
        return dirName
    }


    private fun sourceBackupDirNameFor(syncTask: SyncTask): String {
        return syncTask.sourceBackupDirName ?: taskUniqueDirNameMaker.getUniqueDirName()
    }


    private fun targetBackupDirNameFor(syncTask: SyncTask): String {
        return syncTask.targetBackupDirName ?: taskUniqueDirNameMaker.getUniqueDirName()
    }



    private val dirCreator: DirCreator5 by lazy {
        dirCreator5AssistedFactory.create(syncTask)
    }

    private val taskUniqueDirNameMaker: TaskUniqueDirNameMaker by lazy{
        taskUniqueDirNameMakerAssistedFactory.create(
            syncTask,
            dirNamePrefixSupplier = { appPreferences.BACKUPS_TOP_DIR_PREFIX },
            dirNameSuffixSupplier = { randomUUID },
            appPreferences.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }
}


@AssistedFactory
interface TaskBackupDirPreparerAssistedFactory {
    fun create(syncTask: SyncTask): TaskBackupDirPreparer
}