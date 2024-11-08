package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_dir_maker

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import javax.inject.Inject

class BetterDirMakerCreator @Inject constructor(
    private val dirCreatorAssistedFactory: BetterDirMakerAssistedFactory,
) {
    fun create(syncTask: SyncTask): BetterDirMaker {
        return dirCreatorAssistedFactory.create(syncTask)
    }
}