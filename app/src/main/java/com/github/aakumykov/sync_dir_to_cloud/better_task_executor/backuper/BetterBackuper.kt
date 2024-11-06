package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.backuper

import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.exceptions.TaskExecutionException
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class BetterBackuper @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
) {
    @Throws(TaskExecutionException::class)
    suspend fun backupItems() {

    }
}