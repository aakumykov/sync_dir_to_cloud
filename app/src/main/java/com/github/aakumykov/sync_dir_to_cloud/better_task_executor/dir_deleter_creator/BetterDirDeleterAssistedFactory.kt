package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.dir_deleter_creator

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.AssistedFactory

@AssistedFactory
interface BetterDirDeleterAssistedFactory {
    fun create(syncTask: SyncTask): BetterDirDeleter
}