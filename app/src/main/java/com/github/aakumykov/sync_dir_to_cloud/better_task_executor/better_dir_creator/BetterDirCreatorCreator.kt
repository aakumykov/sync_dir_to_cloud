package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_dir_creator

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import javax.inject.Inject

class BetterDirCreatorCreator @Inject constructor(
    private val dirCreatorAssistedFactory: BetterDirCreatorAssistedFactory,
) {
    fun create(syncTask: SyncTask): BetterDirCreator {
        return dirCreatorAssistedFactory.create(syncTask)
    }
}