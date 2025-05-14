package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.execution

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.task.TaskBackupDirCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.task.TaskBackupDirCreatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import com.github.aakumykov.sync_dir_to_cloud.utils.formattedDateTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Создаёт каталог с уникальным именем
 */
class ExecutionBackupDirCreator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val taskBackupDirCreatorAssistedFactory: TaskBackupDirCreatorAssistedFactory,
    private val appPreferences: AppPreferences,
){
    @Throws(IllegalStateException::class)
    suspend fun createBaseBackupDirInSource(): String {

        generationCounter = 0

        val sourceBackupsDir = syncTask.sourceExecutionBackupDirName
            ?: throw IllegalStateException("There is no source execution backup dir property in task with id='${syncTask.id}'")

        return taskBackupDirCreator.createBackupDirIn(
            SyncSide.SOURCE,
            combineFSPaths(syncTask.sourcePath!!, sourceBackupsDir)
        )
    }


    suspend fun createBaseBackupDirInTarget(): String {

        generationCounter = 0

        val targetBackupsDir = syncTask.targetExecutionBackupDirName
            ?: throw IllegalStateException("There is no target execution backup dir property in task with id='${syncTask.id}'")

        return taskBackupDirCreator.createBackupDirIn(
            SyncSide.TARGET,
            combineFSPaths(syncTask.targetPath!!, targetBackupsDir)
        )
    }


    private val taskBackupDirCreator: TaskBackupDirCreator by lazy {
        taskBackupDirCreatorAssistedFactory.create(
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
        get() = syncTask.lastStart ?: throw IllegalStateException("SyncTask has no info about its starting time")


    // Это поле является "состоянием" объекта.
    // Бесконфликтная работа обеспечена тем, что его значение меняется
    // строго последовательно внутри этого класса.
    private var generationCounter: Int = 0

}


@AssistedFactory
interface ExecutionBackupDirCreatorAssistedFactory {
    fun create(syncTask: SyncTask): ExecutionBackupDirCreator
}
