package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_file_copier

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import javax.inject.Inject

class BetterFileCopierCreator @Inject constructor(
    private val betterFileCopierAssistedFactory: BetterFileCopierAssistedFactory,
){
    fun create(syncTask: SyncTask): BetterFileCopier {
        return betterFileCopierAssistedFactory.create(syncTask)
    }
}