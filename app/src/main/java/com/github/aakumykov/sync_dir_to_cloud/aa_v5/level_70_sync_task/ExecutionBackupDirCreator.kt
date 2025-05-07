package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.cloud_reader.absolutePathFrom
import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreator
import com.github.aakumykov.sync_dir_to_cloud.backuper.BackupDirCreatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskMetadataReader
import com.github.aakumykov.sync_dir_to_cloud.utils.formattedDateTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Создаёт каталог с уникальным именем
 */
class ExecutionBackupDirCreator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val backupDirCreatorAssistedFactory: BackupDirCreatorAssistedFactory,
    private val appPreferences: AppPreferences,
    private val taskMetadataReader: SyncTaskMetadataReader,
){
    /**
     * @param subdirPath Относительный путь к подкаталогу в Источнике.
     */
    suspend fun createBaseBackupDirInSource(subdirPath: String): String {
        generationCounter = 0
        return backupDirCreator.createBackupDirIn(
            SyncSide.SOURCE,
            syncTask.sourcePath!!
        )
    }

    /**
     * @param subdirPath Относительный путь к подкаталогу в Приёмнике.
     */
    suspend fun createBaseBackupDirInTarget(subdirPath: String): String {
        generationCounter = 0
        return backupDirCreator.createBackupDirIn(
            SyncSide.TARGET,
            combineFSPaths(syncTask.targetPath!!, subdirPath)
        )
    }


    private val backupDirCreator: BackupDirCreator by lazy {
        backupDirCreatorAssistedFactory.create(
            syncTask = syncTask,
            dirNamePrefixSupplier = { appPreferences.BACKUPS_DIR_PREFIX },
            dirNameSuffixSupplier = { dirNameSuffix },
            appPreferences.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }


    private val dirNameSuffix: String
        get() = "${formattedDateTime(taskStartTime)}${createExecutionBackupDirAppendix()}"


    private fun createExecutionBackupDirAppendix(): String {
        val appendix = if (0 == generationCounter) { "" } else { "_${generationCounter}" }
        generationCounter++
        return appendix
    }


    private val taskStartTime: Long
        get() = taskMetadataReader.getStartingTime(syncTask.id) ?: throw IllegalStateException("SyncTask has no info about its starting time")


    // Это поле является "состоянием" объекта.
    // Бесконфликтная работа обеспечена тем, что его значение меняется
    // строго последовательно внутри этого класса.
    private var generationCounter: Int = 0

}


@AssistedFactory
interface ExecutionBackupDirCreatorAssistedFactory {
    fun create(syncTask: SyncTask): ExecutionBackupDirCreator
}
