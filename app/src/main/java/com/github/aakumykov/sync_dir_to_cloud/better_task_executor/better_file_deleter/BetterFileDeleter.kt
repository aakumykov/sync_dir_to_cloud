package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_file_deleter

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class BetterFileDeleter @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
) {
    fun deleteDeletedFiles() {
        TODO("Not yet implemented")
    }
}