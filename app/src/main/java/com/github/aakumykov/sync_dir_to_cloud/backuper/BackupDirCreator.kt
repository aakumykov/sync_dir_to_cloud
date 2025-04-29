package com.github.aakumykov.sync_dir_to_cloud.backuper

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class BackupDirCreator @AssistedInject constructor(
    @Assisted private val dirPrefix: String,
    @Assisted private val syncTask: SyncTask,
    private val preferences: AppPreferences,
    private val cloudWriterGetter: CloudWriterGetter,
    private val cloudReaderGetter: CloudReaderGetter,
) {
    /**
     * @return Name of the created dir.
     */
    suspend fun createBackupDirInTarget(syncTask: SyncTask, dirPrefix: String): String {
        return createBackupDir(
            taskId = syncTask.id,
            cloudReader = targetCloudReader,
            cloudWriter = targetCloudWriter,
            backupDirBasePath = syncTask.targetPath!!,
            dirPrefix = dirPrefix
        )
    }


    /**
     * @return Name of the created dir.
     */
    suspend fun createBackupDirInSource(syncTask: SyncTask, dirPrefix: String): String {
        return createBackupDir(
            taskId = syncTask.id,
            cloudReader = sourceCloudReader,
            cloudWriter = sourceCloudWriter,
            backupDirBasePath = syncTask.sourcePath!!,
            dirPrefix = dirPrefix
        )
    }


    /**
     * @return Name of dir for backups in task target dir.
     */
    private suspend fun createBackupDir(
        taskId: String,
        cloudReader: CloudReader,
        cloudWriter: CloudWriter,
        backupDirBasePath: String,
        dirPrefix: String
    ): String {

        val initialDirName = backupDirName(taskId, dirPrefix)

        cloudReader.getFileMetadata(backupDirBasePath, initialDirName).getOrThrow()?.also { metadata ->

            // Если каталог существует, и он пустой, или можно использовать существующий.
            if (metadata.isDir) {
                if (0 == metadata.childCount || preferences.BACKUP_USE_EXISTING_DIR)
                    return initialDirName
            }
        }

        // Объект - файл или каталог, который нельзя использовать.
        return createDirUntilSuccess(
            initialDirName = initialDirName,
            backupDirBasePath = backupDirBasePath,
            cloudReader,
            cloudWriter
        )
    }

    /**
     * @return Name of created dir.
     * @throws RuntimeException if unable to create dir more then [BackupConfig.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT] times.
     */
    @Throws(RuntimeException::class)
    private suspend fun createDirUntilSuccess(
        initialDirName: String,
        backupDirBasePath: String,
        cloudReader: CloudReader,
        cloudWriter: CloudWriter,
        maxAttemptsCount: Int = BackupConfig.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT,
    ): String {

        var tryCount = 0
        var dirNameToCreate = initialDirName

        while (!cloudReader.dirExists(backupDirBasePath, dirNameToCreate).getOrThrow()) {
            dirNameToCreate = newBackupDirName
            cloudWriter.createDir(backupDirBasePath, dirNameToCreate)

            tryCount++
            if (tryCount > maxAttemptsCount)
                throw RuntimeException("Unable to create dir '$dirNameToCreate' in path '$backupDirBasePath' for $tryCount times.")
        }

        return dirNameToCreate
    }


    private val sourceCloudWriter: CloudWriter by lazy {
        cloudWriterGetter.getSourceCloudWriter(syncTask)
    }

    private val targetCloudWriter: CloudWriter by lazy {
        cloudWriterGetter.getTargetCloudWriter(syncTask)
    }

    private val targetCloudReader: CloudReader by lazy {
        cloudReaderGetter.getTargetCloudReaderFor(syncTask)
    }

    private val sourceCloudReader: CloudReader by lazy {
        cloudReaderGetter.getSourceCloudReaderFor(syncTask)
    }

    private fun backupDirName(taskId: String, dirPrefix: String): String = "${dirPrefix}_${taskId}"

    private val newBackupDirName: String get() = "${dirPrefix}_${randomUUID}"
}


@AssistedFactory
interface BackupDirCreatorAssistedFactory {
    fun create(syncTask: SyncTask): BackupDirCreator
}
