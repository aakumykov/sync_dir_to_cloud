package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import com.github.aakumykov.sync_dir_to_cloud.utils.backupDirFormattedDateTime
import com.github.aakumykov.sync_dir_to_cloud.utils.formattedDateTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ExecutionBackupDirPreparer3 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val appPreferences: AppPreferences,
    private val dirCreator5AssistedFactory: DirCreator5AssistedFactory,
    private val executionUniqueDirNameMakerAssistedFactory: ExecutionUniqueDirNameMakerAssistedFactory,
) {
    suspend fun prepareSourceExecutionBackupDir(taskSourceBackupsDirName: String): String {
        val dirName = sourceBackupDirNameFor(syncTask)
        dirCreator.createDirInSource(
            basePath = combineFSPaths(syncTask.sourcePath!!, taskSourceBackupsDirName),
            dirName = dirName)
        return dirName
    }

    suspend fun prepareTargetExecutionBackupDir(taskTargetBackupsDirName: String): String {
        val dirName = targetBackupDirNameFor(syncTask)
        dirCreator.createDirInTarget(
            basePath = combineFSPaths(syncTask.targetPath!!, taskTargetBackupsDirName),
            dirName = dirName)
        return dirName
    }



    private fun sourceBackupDirNameFor(syncTask: SyncTask): String {
        return uniqueDirNameMaker.getUniqueDirName()
    }


    private fun targetBackupDirNameFor(syncTask: SyncTask): String {
        return uniqueDirNameMaker.getUniqueDirName()
    }



    private val dirCreator: DirCreator5 by lazy {
        dirCreator5AssistedFactory.create(syncTask)
    }

    private val uniqueDirNameMaker: ExecutionUniqueDirNameMaker by lazy{
        executionUniqueDirNameMakerAssistedFactory.create(
            syncTask,
            dirNamePrefixSupplier = { appPreferences.BACKUPS_DIR_PREFIX },
            dirNameSuffixSupplier = { backupDirFormattedDateTime },
            appPreferences.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }
}


@AssistedFactory
interface ExecutionBackupDirPreparer3AssistedFactory {
    fun create(syncTask: SyncTask): ExecutionBackupDirPreparer3
}