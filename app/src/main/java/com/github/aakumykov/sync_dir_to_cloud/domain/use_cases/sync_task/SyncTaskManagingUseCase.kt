package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskManager
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskUpdater

class SyncTaskManagingUseCase(
    private val mSyncTaskManager: iSyncTaskManager,
    private val mISyncTaskUpdater: iSyncTaskUpdater
) {

    fun listSyncTasks(): LiveData<List<SyncTask>> {
        return mSyncTaskManager.listSyncTasks()
    }


    fun addSyncTask(syncTask: SyncTask?) {
        mSyncTaskManager.createSyncTask(syncTask)
    }


    fun updateSyncTask(syncTask: SyncTask?) {
        mISyncTaskUpdater.updateSyncTask(syncTask)
    }


    fun getSyncTask(id: String?): SyncTask? {
        return mSyncTaskManager.getSyncTask(id)
    }


    fun deleteSyncTask(syncTask: SyncTask?) {
        mSyncTaskManager.deleteSyncTask(syncTask)
    }
}