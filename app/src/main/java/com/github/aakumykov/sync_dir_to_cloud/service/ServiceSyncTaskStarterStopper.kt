package com.github.aakumykov.sync_dir_to_cloud.service

import android.content.Context
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper
import javax.inject.Inject

class ServiceSyncTaskStarterStopper @Inject constructor(
    @param:AppContext private val appContext: Context
): SyncTaskStarterStopper {

    override suspend fun startSyncTask(syncTask: SyncTask) {
        val intent = SyncTaskService.intentForStart(appContext,syncTask.id)
        appContext.startService(intent)
    }

    override suspend fun stopSyncTask(syncTask: SyncTask) {
        val intent = SyncTaskService.intentForStop(appContext)
        appContext.stopService(intent)
    }
}