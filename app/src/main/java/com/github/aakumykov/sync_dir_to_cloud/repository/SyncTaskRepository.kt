package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.data_sources.SyncTaskLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AppScope
class SyncTaskRepository @Inject constructor(
    private val syncTaskLocalDataSource: SyncTaskLocalDataSource,
    private val coroutineScope: CoroutineScope,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher // FIXME: не нравится мне это здесь
) : SyncTaskCreatorDeleter, SyncTaskReader, SyncTaskUpdater
{
    override suspend fun listSyncTasks(): LiveData<List<SyncTask>> {
        return syncTaskLocalDataSource.listSyncTasks()
    }

    override suspend fun getSyncTask(id: String): SyncTask {
        return syncTaskLocalDataSource.getTask(id)
    }

    override suspend fun createSyncTask(syncTask: SyncTask) {
        syncTaskLocalDataSource.addTask(syncTask)
    }

    override suspend fun deleteSyncTask(syncTask: SyncTask) {
        syncTaskLocalDataSource.delete(syncTask)
    }

    override fun updateSyncTask(syncTask: SyncTask) {
        coroutineScope.launch(coroutineDispatcher) {
            syncTaskLocalDataSource.update(syncTask)
        }
    }
}