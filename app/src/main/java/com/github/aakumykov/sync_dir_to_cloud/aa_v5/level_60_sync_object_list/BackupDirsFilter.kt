package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list

import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.sourceTaskBackupsDirPath
import com.github.aakumykov.sync_dir_to_cloud.extensions.targetTaskBackupsDirPath
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Задача класса - отфильтровывать из списка файлов
 * каталоги бекапа, чтобы не бекапить бекапы.
 */
class BackupDirsFilter @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
) {
    fun isBackupDir(syncSide: SyncSide,
                    fileListItem: RecursiveDirReader.FileListItem
    ): Boolean {
        return fileListItem.absolutePath.startsWith(
            when(syncSide) {
                SyncSide.SOURCE -> sourceBackupsDirPath ?: fileListItem.absolutePath.reversed()
                SyncSide.TARGET -> targetBackupsDirPath ?: fileListItem.absolutePath.reversed()
            }
        )
    }

    private val sourceBackupsDirPath: String? by lazy {
        syncTask.sourceTaskBackupsDirPath
    }

    private val targetBackupsDirPath: String? by lazy {
        syncTask.targetTaskBackupsDirPath
    }
}


@AssistedFactory
interface BackupDirsFilterAssistedFactory {
    fun create(syncTask: SyncTask): BackupDirsFilter
}