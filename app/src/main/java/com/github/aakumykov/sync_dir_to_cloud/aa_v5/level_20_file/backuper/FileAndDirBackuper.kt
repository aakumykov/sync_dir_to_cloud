package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.backuper

import android.util.Pair
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.currentSourceBackupsDir
import com.github.aakumykov.sync_dir_to_cloud.extensions.currentTargetBackupsDir
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import com.github.aakumykov.sync_dir_to_cloud.functions.fileNameFromPath
import com.github.aakumykov.sync_dir_to_cloud.functions.relativeParentDirPath
import dagger.assisted.Assisted
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
    fun backupFileByCopy(absolutePath: String) {

        val paths: SourceTargetPath = absolutePathsFor(absolutePath)

        cloudWriter.copyFile(
            fromAbsolutePath = paths.sourceAbsolutePath,
            toAbsolutePath = paths.targetAbsolutePath,
            overwriteIfExists = true
        )
    }


    @Throws(IllegalArgumentException::class)
    fun backupFileByMove(absolutePath: String) {

        val paths: SourceTargetPath = absolutePathsFor(absolutePath)

        cloudWriter.moveFileOrEmptyDir(
            fromAbsolutePath = paths.sourceAbsolutePath,
            toAbsolutePath = paths.targetAbsolutePath,
            overwriteIfExists = true
        )
    }


    @Throws(IllegalStateException::class)
    private fun createDirInBackupsDir(relativeDirPath: String): String {
        return cloudWriter.createDir(
            backupsDirPathFor(syncSide),
            relativeDirPath
        )
    }


    @Throws(IllegalStateException::class)
    private fun backupsDirPathFor(syncSide: SyncSide): String {
        return when(syncSide) {
            SyncSide.SOURCE -> syncTask.currentSourceBackupsDir
            SyncSide.TARGET -> syncTask.currentTargetBackupsDir
        }.let { path ->
            path ?: throw IllegalStateException("SyncTask does not contains full info about backup dirs: $syncTask")
        }
    }


    private val storageRootPath: String get() = when(syncSide) {
        SyncSide.SOURCE -> syncTask.sourcePath!!
        SyncSide.TARGET -> syncTask.targetPath!!
    }


    private fun absolutePathsFor(absolutePath: String): SourceTargetPath {

        val fileName = fileNameFromPath(absolutePath)
        val relativeParentDirPath = relativeParentDirPath(absolutePath, storageRootPath)

        val targetParentDirPathInBackups = createDirInBackupsDir(relativeParentDirPath)
        val targetFilePath = combineFSPaths(targetParentDirPathInBackups, fileName)

        return SourceTargetPath(
            absolutePath,
            targetFilePath
        )
    }


    private val cloudWriter by lazy {
        when(syncSide) {
            SyncSide.SOURCE -> cloudWriterGetter.getSourceCloudWriter(syncTask)
            SyncSide.TARGET -> cloudWriterGetter.getTargetCloudWriter(syncTask)
        }
    }

    private class SourceTargetPath(
        val sourceAbsolutePath: String,
        val targetAbsolutePath: String,
    ) : Pair<String,String>(sourceAbsolutePath, targetAbsolutePath)
}