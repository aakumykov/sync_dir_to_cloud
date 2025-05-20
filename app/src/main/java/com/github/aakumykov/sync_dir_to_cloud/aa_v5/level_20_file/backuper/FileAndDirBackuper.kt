package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.backuper

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.executionBackupDirRelativePathInTarget
import com.github.aakumykov.sync_dir_to_cloud.extensions.executionBackupDirRelativePathInSource
import com.github.aakumykov.sync_dir_to_cloud.extensions.sourceExecutionBackupDirPath
import com.github.aakumykov.sync_dir_to_cloud.extensions.targetExecutionBackupDirPath
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import com.github.aakumykov.sync_dir_to_cloud.functions.fileNameFromPath
import com.github.aakumykov.sync_dir_to_cloud.functions.relativeParentDirPath
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FileAndDirBackuper @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val syncSide: SyncSide,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    // FIXME: кто должен отвечать за то, чтобы подвергался бекапу только пустой каталог?

    /**
     * Создаёт каталог в каталоге бекапов Задачи, сохраняя относительный путь.
     * Например:
     * - каталог для бекапов: /path/to/backups/backup1
     * - бекапируемый каталог: SOURCE/dir1/dir2
     * - забекапленный каталог: /path/to/backups/backup1/dir1/dir2
     */
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun backupDir(absolutePath: String) {

        val dirName = fileNameFromPath(absolutePath)
        val relativeParentDirPath = relativeParentDirPath(absolutePath, storageRootPath)
        val backupDirPathForDir = combineFSPaths(backupsDirPathFor(syncSide), relativeParentDirPath)

        cloudWriter.createDir(backupDirPathForDir, dirName)
    }


    @Throws(IllegalArgumentException::class)
    fun backupFileByCopy(sourceFileAbsolutePath: String) {

        val targetFilePath = combineFSPaths(
            syncTask.executionBackupDirRelativePathInTarget!!,
            fileNameFromPath(sourceFileAbsolutePath)
        )

        cloudWriter.copyFile(
            fromAbsolutePath = sourceFileAbsolutePath,
            toAbsolutePath = targetFilePath,
            overwriteIfExists = true
        )
    }


    @Throws(IllegalArgumentException::class, RuntimeException::class)
    fun backupFileByMove(backupingFileAbsolutePath: String, syncSide: SyncSide) {

        val backupDirAbsolutePath = when(syncSide) {
            SyncSide.SOURCE -> syncTask.sourceExecutionBackupDirPath!!
            SyncSide.TARGET -> syncTask.targetExecutionBackupDirPath!!
        }

        val destinationPath = combineFSPaths(
            backupDirAbsolutePath,
            fileNameFromPath(backupingFileAbsolutePath)
        )

        cloudWriter.moveFileOrEmptyDir(
            fromAbsolutePath = backupingFileAbsolutePath,
            toAbsolutePath = destinationPath,
            overwriteIfExists = true
        ).also { isMoved ->
            if (!isMoved)
                throw RuntimeException("File '$backupingFileAbsolutePath' not moved to '$destinationPath' in '$syncSide'.")
        }
    }


    @Throws(IllegalStateException::class)
    private fun backupsDirPathFor(syncSide: SyncSide): String {
        return when(syncSide) {
            SyncSide.SOURCE -> syncTask.sourceExecutionBackupDirPath
            SyncSide.TARGET -> syncTask.targetExecutionBackupDirPath
        }.let { path ->
            path ?: throw IllegalStateException("SyncTask does not contains full info about backup dirs: $syncTask")
        }
    }


    private val storageRootPath: String get() = when(syncSide) {
        SyncSide.SOURCE -> syncTask.sourcePath!!
        SyncSide.TARGET -> syncTask.targetPath!!
    }


    private val cloudWriter by lazy {
        when(syncSide) {
            SyncSide.SOURCE -> cloudWriterGetter.getSourceCloudWriter(syncTask)
            SyncSide.TARGET -> cloudWriterGetter.getTargetCloudWriter(syncTask)
        }
    }
}


@AssistedFactory
interface FileAndDirBackuperAssistedFactory {
    fun create(syncTask: SyncTask, syncSide: SyncSide): FileAndDirBackuper
}