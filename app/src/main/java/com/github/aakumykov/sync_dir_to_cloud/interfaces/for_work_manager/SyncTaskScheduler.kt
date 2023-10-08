package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskScheduler {

    fun scheduleSyncTask(syncTask: SyncTask, callbacks: ScheduleCallbacks)
    fun unScheduleSyncTask(syncTask: SyncTask, callbacks: UnScheduleCallbacks)

    interface ScheduleCallbacks {
        fun onSyncTaskScheduleSuccess()
        fun onSyncTaskScheduleError(e: Exception)
    }

    interface UnScheduleCallbacks {
        fun onSyncTaskUnScheduleSuccess()
        fun onSyncTaskUnScheduleError(e: Exception)
    }
}