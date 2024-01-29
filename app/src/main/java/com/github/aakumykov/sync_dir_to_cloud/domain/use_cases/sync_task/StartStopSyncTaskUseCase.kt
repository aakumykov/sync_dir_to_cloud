package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import android.util.Log
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

    private suspend fun startSyncTask(syncTask: SyncTask) {
        Log.d(TAG, "startSyncTask(${syncTask.id})")
        syncTaskStarterStopper.startSyncTask(syncTask)
    }

    // TODO: устанавливать более специфичное состояние?
    private suspend fun stopSyncTask(syncTask: SyncTask) {
        syncTaskStarterStopper.stopSyncTask(syncTask)
    }

    companion object {
        val TAG: String = StartStopSyncTaskUseCase::class.java.simpleName
    }
}