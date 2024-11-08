package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.beter_source_reader

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import javax.inject.Inject

class BetterSourceReaderCreator @Inject constructor(
    private val betterSourceReaderAssistedFactory: BetterSourceReaderAssistedFactory,
){
    fun createSourceReader(syncTask: SyncTask): BetterSourceReader {
        return betterSourceReaderAssistedFactory.create(syncTask)
    }
}