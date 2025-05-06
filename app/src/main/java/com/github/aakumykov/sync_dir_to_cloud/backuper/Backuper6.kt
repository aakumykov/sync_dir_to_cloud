package com.github.aakumykov.sync_dir_to_cloud.backuper

import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class Backuper6 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask
) {
//    @Inject
//    lateinit var backupDirCreatorFactory: BackupDirCreatorAssistedFactory

    /*val backupDirCreator by lazy { backupDirCreatorFactory.create(
        dirPrefix = BackupConfig.BACKUPS_TOP_DIR_PREFIX,
        syncTask = syncTask
    ) }*/

    init {
        appComponent.injectBackuper6(this)
    }
}


@AssistedFactory
interface Backuper6AssistedFactory {
    fun create(syncTask: SyncTask): Backuper6
}