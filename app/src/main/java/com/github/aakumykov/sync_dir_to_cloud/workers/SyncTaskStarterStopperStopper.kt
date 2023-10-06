package com.github.aakumykov.sync_dir_to_cloud.workers

import androidx.work.WorkManager
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper
import javax.inject.Inject

class SyncTaskStarterStopperStopper @Inject constructor(private val workManager: WorkManager) :
    SyncTaskStarterStopper {

    override fun startSyncTask(syncTask: SyncTask, callbacks: SyncTaskStarterStopper.StartCallbacks) {

        /*val oneTimeWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<>()
            .build();

        workManager.enqueueUniqueWork(

        )*/
    }

    override fun stopSyncTask(syncTask: SyncTask, callbacks: SyncTaskStarterStopper.StopCallbacks) {

    }
}