package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager

import com.github.aakumykov.entities.SyncTask

interface SyncTaskStarterStopper {
    fun startSyncTask(syncTask: SyncTask)
    fun stopSyncTask(syncTask: SyncTask, callback: StopCallback)

    interface StopCallback {
        fun onSyncTaskStopped(taskId: String)
    }
}
