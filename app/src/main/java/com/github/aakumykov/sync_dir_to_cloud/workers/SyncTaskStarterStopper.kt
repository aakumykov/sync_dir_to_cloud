package com.github.aakumykov.sync_dir_to_cloud.workers

import androidx.work.WorkManager
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStopper

class SyncTaskStarterStopper(private val workManager: WorkManager) : SyncTaskStarter,
    SyncTaskStopper {

    override fun startSyncTask(syncTask: SyncTask, callbacks: SyncTaskStarter.Callbacks) {

    }

    override fun stopSyncTask(syncTask: SyncTask, callbacks: SyncTaskStopper.Callbacks) {

    }
}