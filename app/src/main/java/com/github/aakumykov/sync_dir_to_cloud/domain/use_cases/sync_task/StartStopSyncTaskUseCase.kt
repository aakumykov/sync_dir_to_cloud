package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper
import javax.inject.Inject

// FIXME: если Starter-Stopper является простой обёрткой над WorkManager-ом, то можно его убрать.
class StartStopSyncTaskUseCase @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStarterStopper: SyncTaskStarterStopper
) {
    suspend fun startStopSyncTask(taskId: String) {

        val syncTask = syncTaskReader.getSyncTask(taskId)

        when (syncTask.executionState) {
            ExecutionState.RUNNING -> stopSyncTask(syncTask)
            ExecutionState.IDLE -> startSyncTask(syncTask)
            ExecutionState.ERROR -> startSyncTask(syncTask)
        }
    }


    private suspend fun startSyncTask(syncTask: SyncTask) {
        syncTaskStarterStopper.startSyncTask(syncTask)
    }


    suspend fun stopSyncTask(syncTask: SyncTask) {
        syncTaskStarterStopper.stopSyncTask(syncTask)
    }


    companion object {
        val TAG: String = StartStopSyncTaskUseCase::class.java.simpleName
    }
}