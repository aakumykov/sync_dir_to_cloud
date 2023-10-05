package com.github.aakumykov.sync_dir_to_cloud.interfaces


import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface iSyncTaskStarter {

    fun startSyncTask(syncTask: SyncTask, callbacks: Callbacks)

    interface Callbacks {
        fun onSyncTaskStarted()
        fun onSyncTaskStartingError(e: Exception)
    }
}
