package com.github.aakumykov.sync_dir_to_cloud.workers

import androidx.work.WorkManager
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskStarter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskStopper

class SyncTaskStarterStopper(private val workManager: WorkManager) : iSyncTaskStarter, iSyncTaskStopper {

    override fun startSyncTask(syncTask: SyncTask, callbacks: iSyncTaskStarter.Callbacks) {

    }

    override fun stopSyncTask(syncTask: SyncTask, callbacks: iSyncTaskStopper.Callbacks) {

    }
}