package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskStarterStopper {

    fun startSyncTask(syncTask: SyncTask, callbacks: StartCallbacks)
    fun stopSyncTask(syncTask: SyncTask, callbacks: StopCallbacks)

    interface StartCallbacks {
        fun onSyncTaskStarted()
        fun onSyncTaskStartingError(e: Exception)
    }

    interface StopCallbacks {
        fun onSyncTaskStopped()
        fun onSyncTaskStoppingError(e: Exception)
    }
}
