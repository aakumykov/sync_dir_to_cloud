package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_dir_creator

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class BetterDirCreator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
) {
    suspend fun createDirs() {

    }
}