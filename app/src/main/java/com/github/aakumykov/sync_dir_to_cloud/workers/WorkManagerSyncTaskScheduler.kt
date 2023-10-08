package com.github.aakumykov.sync_dir_to_cloud.workers

import androidx.work.WorkManager
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler
import javax.inject.Inject

class WorkManagerSyncTaskScheduler @Inject constructor(
    private val workManager: WorkManager,
) : SyncTaskScheduler {

    override fun scheduleSyncTask(
        syncTask: SyncTask,
        callbacks: SyncTaskScheduler.ScheduleCallbacks
    ) {

    }

    override fun unScheduleSyncTask(
        syncTask: SyncTask,
        callbacks: SyncTaskScheduler.UnScheduleCallbacks
    ) {

    }
}