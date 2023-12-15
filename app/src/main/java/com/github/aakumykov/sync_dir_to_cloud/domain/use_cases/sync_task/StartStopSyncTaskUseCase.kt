package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper
import javax.inject.Inject

class StartStopSyncTaskUseCase @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStarterStopper: SyncTaskStarterStopper,
    private val syncTaskUpdater: SyncTaskUpdater
) {
    suspend fun startStopSyncTask(taskId: String) {

        // TODO: кинуть исключение просто так
        val syncTask = syncTaskReader.getSyncTask(taskId)

        if (syncTask.state == SyncTask.State.WRITING_TARGET) {
            syncTaskStarterStopper.stopSyncTask(syncTask, object:SyncTaskStarterStopper.StopCallback {
                override fun onSyncTaskStopped(taskId: String) {
                    // FIXME: устанавливать более специфичное состояние?
                    syncTask.state = SyncTask.State.IDLE
                    syncTaskUpdater.updateSyncTask(syncTask)
                }
            })
        }
        else
            syncTaskStarterStopper.startSyncTask(syncTask)
    }

    fun stopSyncTask(syncTask: SyncTask?) {

    }

    private suspend fun getSyncTask(taskId: String): SyncTask {
        val syncTask = syncTaskReader.getSyncTask(taskId)
        return syncTask
    }

    companion object {
        private val TAG = StartStopSyncTaskUseCase::class.java.simpleName
    }
}