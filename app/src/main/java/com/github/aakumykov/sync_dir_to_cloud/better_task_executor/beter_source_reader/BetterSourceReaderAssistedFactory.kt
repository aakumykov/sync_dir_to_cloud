package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.beter_source_reader

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.AssistedFactory

@AssistedFactory
interface BetterSourceReaderAssistedFactory {
    fun create(syncTask: SyncTask): BetterSourceReader
}