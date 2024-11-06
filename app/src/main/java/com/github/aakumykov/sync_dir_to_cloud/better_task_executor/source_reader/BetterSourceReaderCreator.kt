package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.source_reader

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import javax.inject.Inject

class BetterSourceReaderCreator @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val betterSourceReaderAssistedFactory: BetterSourceReaderAssistedFactory,
){
    suspend fun createSourceReader(syncTask: SyncTask): BetterSourceReader {
        return betterSourceReaderAssistedFactory.create(
            syncTaskReader.getSyncTask(syncTask.id)
        )
    }
}