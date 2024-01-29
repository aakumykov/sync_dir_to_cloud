package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper
import javax.inject.Inject

class StartStopSyncTaskUseCase @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStarterStopper: SyncTaskStarterStopper
) {
    suspend fun startStopSyncTask(taskId: String) {

        val syncTask = syncTaskReader.getSyncTask(taskId)

        // TODO: поле в SyncTask "isRunning" ?..
        when (syncTask.state) {
            in arrayOf(
                SyncTask.State.WRITING_TARGET,
                SyncTask.State.READING_SOURCE
            ) -> stopSyncTask(syncTask)
            else -> startSyncTask(syncTask)
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