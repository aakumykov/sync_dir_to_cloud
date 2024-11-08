package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_backuper

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import javax.inject.Inject

class BetterBackuperCreator @Inject constructor(
    private val betterBackuperAssistedFactory: BetterBackuperAssistedFactory
) {
    fun createBackuper(syncTask: SyncTask): BetterBackuper {
        return betterBackuperAssistedFactory.create(syncTask)
    }
}