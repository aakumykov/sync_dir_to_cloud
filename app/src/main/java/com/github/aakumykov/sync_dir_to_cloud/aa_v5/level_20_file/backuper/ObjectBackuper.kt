package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.backuper

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.currentSourceBackupsDir
import com.github.aakumykov.sync_dir_to_cloud.extensions.currentTargetBackupsDir
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class ObjectBackuper @AssistedInject constructor(
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
    suspend fun backupDir(syncObject: SyncObject) {

        if (!syncObject.isDir)
            throw IllegalArgumentException("SyncObject argument is not dir: $syncObject")

        val backupDirPath: String = backupsDirPathFor(syncSide)
            ?: throw IllegalStateException("SyncTask does not contains full info about backup dirs: $syncTask")

        val extendedBackupDirPath = combineFSPaths(backupDirPath, syncObject.relativeParentDirPath)

        val dirName = syncObject.name

        cloudWriter.createDir(extendedBackupDirPath, dirName)
    }


    @Throws(IllegalArgumentException::class)
    fun backupFileByCopy(syncObject: SyncObject) {

        if (!syncObject.isFile)
            throw IllegalArgumentException("SyncObject argument is not dir: $syncObject")

        val targetParentDirPathInBackups = createDirInBackupsDir(syncObject.relativeParentDirPath)

        cloudWriter.copyFile(
            fromAbsolutePath = syncObject.absolutePathIn(syncTask.sourcePath!!),
            toAbsolutePath = combineFSPaths(targetParentDirPathInBackups, syncObject.name),
            overwriteIfExists = true
        )
    }


    @Throws(IllegalArgumentException::class)
    fun backupFileByMove(syncObject: SyncObject) {

        if (!syncObject.isFile)
            throw IllegalArgumentException("SyncObject argument is not dir: $syncObject")

        val targetParentDirPathInBackups = createDirInBackupsDir(syncObject.relativeParentDirPath)

        cloudWriter.moveFileOrEmptyDir(
            fromAbsolutePath = syncObject.absolutePathIn(syncTask.sourcePath!!),
            toAbsolutePath = combineFSPaths(targetParentDirPathInBackups, syncObject.name),
            overwriteIfExists = true
        )
    }


    @Throws(IllegalStateException::class)
    private fun createDirInBackupsDir(relativeDirPath: String): String {

        val backupsDirPath = backupsDirPathFor(syncSide)
            ?: throw IllegalStateException("SyncTask does not contains full info about backup dirs: $syncTask")

        return cloudWriter.createDir(backupsDirPath, relativeDirPath)
    }


    private fun backupsDirPathFor(syncSide: SyncSide): String? {
        return when(syncSide) {
            SyncSide.SOURCE -> syncTask.currentSourceBackupsDir
            SyncSide.TARGET -> syncTask.currentTargetBackupsDir
        }
    }


    private val cloudWriter by lazy {
        when(syncSide) {
            SyncSide.SOURCE -> cloudWriterGetter.getSourceCloudWriter(syncTask)
            SyncSide.TARGET -> cloudWriterGetter.getTargetCloudWriter(syncTask)
        }
    }
}