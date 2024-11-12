package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.bas_state_resetter

import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.exceptions.TaskExecutionException
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import javax.inject.Inject

class BadStatesResetter @Inject constructor() {

    @Throws(TaskExecutionException.CriticalException.ResettingBadStatesException::class)
    suspend fun resetBadStates(syncTask: SyncTask) {

    }
}