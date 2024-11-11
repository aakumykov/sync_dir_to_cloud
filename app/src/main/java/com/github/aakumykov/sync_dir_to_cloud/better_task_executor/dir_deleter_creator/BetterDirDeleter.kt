package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.dir_deleter_creator

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class BetterDirDeleter @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
) {
    fun deleteDeletedDirs() {
        TODO("Not yet implemented")
    }
}