package com.github.aakumykov.sync_dir_to_cloud.repository.sync_task

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskManager
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.sync_task.data_sources.SyncTaskLocalDataSource

class SyncTaskRepository(private val mSyncTaskLocalDataSource: SyncTaskLocalDataSource) :
    iSyncTaskManager, iSyncTaskUpdater {

    override suspend fun listSyncTasks(): LiveData<List<SyncTask>> {
        return mSyncTaskLocalDataSource.listSyncTasks()
    }

    override suspend fun getSyncTask(id: String): SyncTask? {
        return mSyncTaskLocalDataSource.getTask(id)
    }

    override suspend fun createSyncTask(syncTask: SyncTask) {
        mSyncTaskLocalDataSource.addTask(syncTask)
    }

    override suspend fun deleteSyncTask(syncTask: SyncTask) {
        mSyncTaskLocalDataSource.delete(syncTask)
    }

    override suspend fun updateSyncTask(syncTask: SyncTask) {
        mSyncTaskLocalDataSource.update(syncTask)
    }
}