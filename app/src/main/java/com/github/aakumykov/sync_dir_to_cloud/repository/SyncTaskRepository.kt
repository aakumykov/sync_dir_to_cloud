package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.data_sources.SyncTaskLocalDataSource
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AppScope
class SyncTaskRepository @Inject constructor(
    private val syncTaskDAO: SyncTaskDAO,
    private val syncTaskLocalDataSource: SyncTaskLocalDataSource,
    private val coroutineScope: CoroutineScope,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher // FIXME: не нравится мне это здесь
)
    : SyncTaskCreatorDeleter, SyncTaskReader, SyncTaskUpdater, SyncTaskStateChanger
{
    override suspend fun listSyncTasks(): LiveData<List<SyncTask>> {
        return syncTaskLocalDataSource.listSyncTasks()
    }

    override suspend fun getSyncTask(id: String): SyncTask {
        return syncTaskLocalDataSource.getTask(id)
    }

    override suspend fun getSyncTaskAsLiveData(taskId: String): LiveData<SyncTask> {
        return syncTaskLocalDataSource.getTaskAsLiveData(taskId)
    }

    override suspend fun createSyncTask(syncTask: SyncTask) {
//        syncTaskLocalDataSource.addTask(syncTask)
        syncTaskDAO.addSuspend(syncTask)
    }

    override suspend fun deleteSyncTask(syncTask: SyncTask) {
        syncTaskLocalDataSource.delete(syncTask)
    }

    override fun updateSyncTask(syncTask: SyncTask) {
        coroutineScope.launch(coroutineDispatcher) {
            syncTaskDAO.update(syncTask)
        }
    }

    override fun changeState(taskId: String, newState: SyncTask.State) {
        MyLogger.d(TAG, "changeState($taskId, $newState")
        coroutineScope.launch(coroutineDispatcher) {
            syncTaskLocalDataSource.setState(taskId, newState)
        }
    }

    override fun changeSchedulingState(
        taskId: String,
        newSate: SyncTask.SimpleState,
        errorMsg: String
    ) {
        coroutineScope.launch(coroutineDispatcher) {
            syncTaskLocalDataSource.setSyncTaskSchedulingState(taskId, newSate, errorMsg)
        }
    }

    override suspend fun deleteSyncTask(taskId: String) {
        MyLogger.d(TAG, "deleteSyncTask($taskId)")
        coroutineScope.launch(coroutineDispatcher) {
            syncTaskLocalDataSource.delete(taskId)
        }
    }


    companion object {
        val TAG: String = SyncTaskRepository::class.java.simpleName
    }
}