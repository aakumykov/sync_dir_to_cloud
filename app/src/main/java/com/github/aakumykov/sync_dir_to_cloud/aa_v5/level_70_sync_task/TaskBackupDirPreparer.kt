package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.backup_dir_preparer2.UniqueDirNameMakerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

//
// Задача класса - посмотреть, есть ли имя каталога бекапов в SyncTask,
// если нет, создать такое имя (уникальное, за фиксированное количество попыток),
// создать каталог в хранилище, вернуть вызывающему коду имя такого каталога.
//
class TaskBackupDirPreparer @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val appPreferences: AppPreferences,
    private val uniqueDirNameMakerAssistedFactory: UniqueDirNameMakerAssistedFactory,
    private val cloudReaderGetter: CloudReaderGetter,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    /**
     * @return Имя каталога бекапов.
     */
    suspend fun prepareBackupsDirInSource(): String {
        var isExists = true
        val basePath = syncTask.sourcePath!!
        var dirName: String? = ""
        while (isExists) {
            dirName = uniqueDirNameMaker.getUniqueDirName()
            isExists = dirExists(dirName, parentDirPath = basePath)
        }
        cloudWriterGetter
            .getSourceCloudWriter(syncTask)
            .createDir(basePath = basePath, dirName = dirName!!)
        return dirName
    }

    private suspend fun dirExists(dirName: String, parentDirPath: String): Boolean {
        return cloudReaderGetter
            .getSourceCloudReaderFor(syncTask)
            .dirExists(basePath =  parentDirPath, fileName = dirName)
            .getOrThrow()
    }

    private val uniqueDirNameMaker by lazy {
        uniqueDirNameMakerAssistedFactory.create(
            syncTask,
            dirNamePrefixSupplier = { appPreferences.BACKUPS_TOP_DIR_PREFIX },
            dirNameSuffixSupplier = { randomUUID },
            appPreferences.BACKUP_DIR_CREATION_MAX_ATTEMPTS_COUNT
        )
    }
}


@AssistedFactory
interface TaskBackupDirPreparerAssistedFactory {
    fun create(syncTask: SyncTask): TaskBackupDirPreparer
}