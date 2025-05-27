package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.checker.FileExistenceCheckerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.DirCreator5AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import com.github.aakumykov.sync_dir_to_cloud.utils.backupDirFormattedDateTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ExecutionBackupDirPreparer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val appPreferences: AppPreferences,
    private val fileExistenceCheckerAssistedFactory: FileExistenceCheckerAssistedFactory,
    private val dirCreator5AssistedFactory: DirCreator5AssistedFactory,
    private val executionUniqueDirNameMakerAssistedFactory: ExecutionUniqueDirNameMakerAssistedFactory,
) {
    suspend fun prepareSourceExecutionBackupDir(taskSourceBackupsDirName: String): String {

        val dirName = sourceBackupDirNameFor(syncTask)

        if (fileExistenceChecker.dirNotExistsInSource(dirName))
            dirCreator.createDirInSource(
                basePath = combineFSPaths(syncTask.sourcePath!!, taskSourceBackupsDirName),
                dirName = dirName)

        return dirName
    }

    suspend fun prepareTargetExecutionBackupDir(taskTargetBackupsDirName: String): String {

        val dirName = targetBackupDirNameFor(syncTask)

        if (fileExistenceChecker.dirNotExistsInTarget(dirName))
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


    private val fileExistenceChecker by lazy {
        fileExistenceCheckerAssistedFactory.create(syncTask)
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
interface ExecutionBackupDirPreparerAssistedFactory {
    fun create(syncTask: SyncTask): ExecutionBackupDirPreparer
}