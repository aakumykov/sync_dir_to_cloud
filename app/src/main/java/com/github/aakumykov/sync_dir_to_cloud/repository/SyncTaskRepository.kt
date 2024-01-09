package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
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
)
    : SyncTaskCreatorDeleter, SyncTaskReader, SyncTaskUpdater, SyncTaskStateChanger, SyncTaskStateReader
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

    override fun changeState(taskId: String, newState: SyncTask.State) {
        coroutineScope.launch(coroutineDispatcher) {
            syncTaskLocalDataSource.setState(taskId, newState)
        }
    }

    override suspend fun getSyncTaskState(taskId: String): LiveData<SyncTask.State> {
        return syncTaskLocalDataSource.getTaskState(taskId)
    }
}