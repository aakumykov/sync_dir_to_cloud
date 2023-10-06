package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.data_sources.SyncTaskLocalDataSource
import javax.inject.Inject

class SyncTaskRepository @Inject constructor(
    private val mSyncTaskLocalDataSource: SyncTaskLocalDataSource
) : SyncTaskCreatorDeleter, SyncTaskReader, SyncTaskUpdater
{
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