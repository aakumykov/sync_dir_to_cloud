package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_target_reader

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class BetterTargetReader @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
) {
    fun readTargetFilesState() {

    }
}