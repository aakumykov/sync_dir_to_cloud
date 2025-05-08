package com.github.aakumykov.sync_dir_to_cloud.backuper

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.task.TaskBackupDirCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task.task.TaskBackupDirCreatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.config.AppPreferences
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class FileAndDirBackuper @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val appPreferences: AppPreferences,
) {
    @Inject
    lateinit var backupDirCreatorFactory: TaskBackupDirCreatorAssistedFactory

    private val taskBackupDirCreator: TaskBackupDirCreator by lazy {
        backupDirCreatorFactory.create(
            syncTask = syncTask,
            dirNamePrefixSupplier = { "" },
            dirNameSuffixSupplier = { "" },
            maxCreationAttemptsCount = 5
        )
    }

    init {
        appComponent.injectBackuper6(this)
    }
}


@AssistedFactory
interface FileAndDirBackuperAssistedFactory {
    fun create(syncTask: SyncTask): FileAndDirBackuper
}