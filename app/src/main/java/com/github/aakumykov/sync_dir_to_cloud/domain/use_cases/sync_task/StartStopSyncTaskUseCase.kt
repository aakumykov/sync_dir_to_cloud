package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper
import javax.inject.Inject

class StartStopSyncTaskUseCase @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStarterStopper: SyncTaskStarterStopper,
) {
    suspend fun startSyncTask(taskId: String) {
        // TODO: кинуть исключение просто так
        syncTaskStarterStopper.startSyncTask(getSyncTask(taskId))
    }

    fun stopSyncTask(syncTask: SyncTask?) {

    }

    private suspend fun getSyncTask(taskId: String): SyncTask {
        val syncTask = syncTaskReader.getSyncTask(taskId)
            ?: throw IllegalStateException("SyncTask with id $taskId not found in database.")
        return syncTask
    }

    companion object {
        private val TAG = StartStopSyncTaskUseCase::class.java.simpleName
    }
}