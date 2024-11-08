package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_target_reader

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import javax.inject.Inject

class BetterTargetReaderCreator @Inject constructor(
    private val assistedFactory: BetterTargetReaderAssistedFactory,
) {
    fun createTargetReader(syncTask: SyncTask): BetterTargetReader {
        return assistedFactory.create(syncTask)
    }
}