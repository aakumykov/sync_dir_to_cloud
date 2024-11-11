package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_file_deleter

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.AssistedFactory

@AssistedFactory
interface BetterFileDeleterAssistedFactory {
    fun create(syncTask: SyncTask): BetterFileDeleter
}