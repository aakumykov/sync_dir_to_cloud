package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.backuper

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.AssistedFactory

@AssistedFactory
interface BetterBackuperAssistedFactory {
    fun createBackuper(syncTask: SyncTask): BetterBackuper
}