package com.github.aakumykov.sync_dir_to_cloud.interfaces


import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface iSyncTaskStopper {

    fun stopSyncTask(syncTask: SyncTask, callbacks: Callbacks)

    interface Callbacks {
        fun onSyncTaskStopped()
        fun onSyncTaskStoppingError(e: Exception)
    }
}
