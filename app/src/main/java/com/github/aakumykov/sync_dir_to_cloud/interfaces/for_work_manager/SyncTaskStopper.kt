package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager


import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskStopper {

    fun stopSyncTask(syncTask: SyncTask, callbacks: Callbacks)

    interface Callbacks {
        fun onSyncTaskStopped()
        fun onSyncTaskStoppingError(e: Exception)
    }
}
