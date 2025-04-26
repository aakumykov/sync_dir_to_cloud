package com.github.aakumykov.sync_dir_to_cloud.backuper

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_01_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_01_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig
import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig.Companion.BACKUPS_TOP_DIR_NAME
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.jvm.Throws


class BackupDirCreator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val preferences: AppPreferences,
    private val cloudWriterGetter: CloudWriterGetter,
    private val cloudReaderGetter: CloudReaderGetter,
    private val syncTaskUpdater: SyncTaskUpdater,
) {
    private val backupDirName: String = "${BACKUPS_TOP_DIR_NAME}_${syncTask.id}"
    private val newBackupDirName: String get() = "${BACKUPS_TOP_DIR_NAME}_${randomUUID}"

    suspend fun createBackupDirsFor(syncTask: SyncTask) {
        when (syncTask.syncMode!!) {
            SyncMode.SYNC -> createBackupDirInTarget()
            SyncMode.MIRROR -> {
                createBackupDirInSource()
                createBackupDirInTarget()
            }
        }
    }

    private suspend fun createBackupDirInTarget() {
        val dirName = createBackupDir(targetCloudReader, targetCloudWriter, syncTask.targetPath!!)
        syncTaskUpdater.setTargetBackupDir(syncTask.id, dirName)
    }

    private suspend fun createBackupDirInSource() {
        val dirName = createBackupDir(sourceCloudReader, sourceCloudWriter, syncTask.targetPath!!)
        syncTaskUpdater.setSourceBackupDir(syncTask.id, dirName)
    }


    /**
     * @return Name of dir for backups in task target dir.
     */
    private suspend fun createBackupDir(
        cloudReader: CloudReader,
        cloudWriter: CloudWriter,
        backupDirBasePath: String
    ): String {
        cloudReader.getFileMetadata(backupDirBasePath, backupDirName).getOrThrow()?.also { metadata ->

            // Если каталог существует, и он пустой, или можно использовать существующий.
            if (metadata.isDir) {
                if (0 == metadata.childCount || preferences.BACKUP_USE_EXISTING_DIR)
                    return backupDirName
            }
        }
        // Объект - файл или каталог, который нельзя использовать.
        return createDirUntilSuccess(backupDirBasePath, cloudReader, cloudWriter)
    }

    /**
     * @return Name of created dir.
     * @throws RuntimeException if unable to create dir more then [BackupConfig.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT] times.
     */
    @Throws(RuntimeException::class)
    private suspend fun createDirUntilSuccess(
        backupDirBasePath: String,
        cloudReader: CloudReader,
        cloudWriter: CloudWriter,
        maxAttemptsCount: Int = BackupConfig.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT,
    ): String {

        var tryCount = 0
        var dirNameToCreate = newBackupDirName

        while (cloudReader.dirExists(backupDirBasePath,dirNameToCreate).getOrThrow()) {
            dirNameToCreate = newBackupDirName
            cloudWriter.createDir(backupDirBasePath, backupDirName)

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
}


@AssistedFactory
interface BackupDirCreatorAssistedFactory {
    fun create(syncTask: SyncTask): BackupDirCreator
}
