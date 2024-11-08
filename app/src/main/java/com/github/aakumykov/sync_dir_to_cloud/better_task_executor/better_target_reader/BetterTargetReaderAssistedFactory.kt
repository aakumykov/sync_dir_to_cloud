package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_target_reader

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.AssistedFactory

@AssistedFactory
interface BetterTargetReaderAssistedFactory {
    fun create(syncTask: SyncTask): BetterTargetReader
}