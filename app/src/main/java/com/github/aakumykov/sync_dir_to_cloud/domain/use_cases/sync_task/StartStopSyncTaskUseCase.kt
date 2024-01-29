package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper
import javax.inject.Inject

class StartStopSyncTaskUseCase @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStarterStopper: SyncTaskStarterStopper,
    private val syncTaskStateChanger: SyncTaskStateChanger
) {
    // TODO: поле в SyncTask "isRunning"...
    suspend fun startStopSyncTask(taskId: String) {

        // TODO: кинуть исключение просто так
        val syncTask = syncTaskReader.getSyncTask(taskId)

        when (syncTask.state) {
            SyncTask.State.WRITING_TARGET -> stopSyncTask(syncTask)
            SyncTask.State.READING_SOURCE -> stopSyncTask(syncTask)
            else -> startSyncTask(syncTask)
        }
    }

    suspend fun startSyncTask(taskId: String) {
        syncTaskReader.getSyncTask(taskId).also { startSyncTask(it) }
    }

    suspend fun stopSyncTask(taskId: String) {
        syncTaskReader.getSyncTask(taskId).also { stopSyncTask(it) }
    }

    // TODO: устанавливать более специфичное состояние?
    // FIXME: почему здесь не лямбда за пределами скобок?
    private fun stopSyncTask(syncTask: SyncTask) {
        syncTaskStarterStopper.stopSyncTask(syncTask, object:SyncTaskStarterStopper.StopCallback {
            override fun onSyncTaskStopped(taskId: String) {
                syncTaskStateChanger.changeState(syncTask.id, SyncTask.State.IDLE)
            }
        })
    }

    private fun startSyncTask(syncTask: SyncTask) {
        syncTaskStarterStopper.startSyncTask(syncTask)
    }
}