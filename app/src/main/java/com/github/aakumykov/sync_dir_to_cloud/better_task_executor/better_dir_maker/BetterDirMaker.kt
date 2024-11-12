package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_dir_maker

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class BetterDirMaker @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
) {
    suspend fun createNewDirs() {

    }

    fun createNeverSyncedDirs() {

    }

    fun createLostDirsAgain() {

    }
}