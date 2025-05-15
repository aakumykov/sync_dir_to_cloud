package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.BackupDirCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.BackupDirCreatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.IOException

//
// Задача класса - посмотреть, есть ли имя каталога бекапов в SyncTask,
// если нет, создать такое имя (уникальное, за фиксированное количество попыток),
// создать каталог в хранилище, вернуть вызывающему коду имя такого каталога.
//
class TaskBackupDirCreator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val backupDirCreatorAssistedFactory: BackupDirCreatorAssistedFactory,
    private val appPreferences: AppPreferences,
) {
    /**
     * @return Имя каталога.
     */
    @Throws(IOException::class, CloudWriter.OperationUnsuccessfulException::class)
    suspend fun createTaskBackupDirIn(syncSide: SyncSide, dirPathToCreateIn: String): String {

        val dirName = when(syncSide) {
            SyncSide.SOURCE -> syncTask.sourceBackupDirName
            SyncSide.TARGET -> syncTask.targetBackupDirName
        } ?: getUniqueDirName(syncSide, dirPathToCreateIn)

        backupDirCreator.
    }


    private val backupDirCreator: BackupDirCreator by lazy {
        backupDirCreatorAssistedFactory.create(
            syncTask = syncTask,
            dirNamePrefixSupplier = { appPreferences.BACKUPS_TOP_DIR_PREFIX },
            dirNameSuffixSupplier = { randomUUID },
            appPreferences.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }
}


@AssistedFactory
interface TaskBackupDirPreparerAssistedFactory {
    fun create(syncTask: SyncTask): TaskBackupDirCreator
}