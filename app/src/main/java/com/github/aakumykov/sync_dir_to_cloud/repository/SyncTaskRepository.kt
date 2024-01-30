package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskSchedulingStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskStateDAO
import javax.inject.Inject

@AppScope
class SyncTaskRepository @Inject constructor(
    private val syncTaskDAO: SyncTaskDAO,
    private val syncTaskStateDAO: SyncTaskStateDAO,
    private val syncTaskSchedulingStateDAO: SyncTaskSchedulingStateDAO
)
    : SyncTaskCreatorDeleter, SyncTaskReader, SyncTaskUpdater, SyncTaskStateChanger
{
    override suspend fun listSyncTasks(): LiveData<List<SyncTask>> {
//        return syncTaskLocalDataSource.listSyncTasks()
        return syncTaskDAO.list()
    }

    override suspend fun getSyncTask(id: String): SyncTask {
//        return syncTaskLocalDataSource.getTask(id)
        return syncTaskDAO.get(id)
    }

    override suspend fun getSyncTaskAsLiveData(taskId: String): LiveData<SyncTask> {
//        return syncTaskLocalDataSource.getTaskAsLiveData(taskId)
        return syncTaskDAO.getAsLiveData(taskId)
    }

    override suspend fun createSyncTask(syncTask: SyncTask) {
//        syncTaskLocalDataSource.addTask(syncTask)
        syncTaskDAO.add(syncTask)
    }

    override suspend fun deleteSyncTask(syncTask: SyncTask) {
//        syncTaskLocalDataSource.delete(syncTask)
        return syncTaskDAO.delete(syncTask)
    }

    override suspend fun updateSyncTask(syncTask: SyncTask) {
        /*coroutineScope.launch(coroutineDispatcher) {
            syncTaskLocalDataSource.update(syncTask)
        }*/
        syncTaskDAO.update(syncTask)
    }

    override suspend fun changeState(taskId: String, newState: SyncTask.State) {
        /*coroutineScope.launch(coroutineDispatcher) {
            syncTaskLocalDataSource.setState(taskId, newState)
        }*/
        syncTaskStateDAO.setState(taskId, newState)
    }

    override suspend fun changeSchedulingState(taskId: String, newSate: SyncTask.SimpleState, errorMsg: String) {
        /*coroutineScope.launch(coroutineDispatcher) {
            syncTaskLocalDataSource.syncTaskSchedulingState(taskId, newSate, errorMsg)
        }*/
        when(newSate) {
            SyncTask.SimpleState.IDLE -> syncTaskSchedulingStateDAO.setIdleState(taskId)
            SyncTask.SimpleState.BUSY -> syncTaskSchedulingStateDAO.setBusyState(taskId)
            SyncTask.SimpleState.ERROR -> syncTaskSchedulingStateDAO.setErrorState(taskId, errorMsg)
        }
    }

    override suspend fun deleteSyncTask(taskId: String) {
        /*coroutineScope.launch(coroutineDispatcher) {
            syncTaskLocalDataSource.delete(taskId)
        }*/
        syncTaskDAO.delete(taskId)
    }


    companion object {
        val TAG: String = SyncTaskRepository::class.java.simpleName
    }
}