package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager


import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskStarter {

    fun startSyncTask(syncTask: SyncTask, callbacks: Callbacks)

    interface Callbacks {
        fun onSyncTaskStarted()
        fun onSyncTaskStartingError(e: Exception)
    }
}
