package com.github.aakumykov.sync_dir_to_cloud.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskMetadataReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskCreatorDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskRunningTimeUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncStateChanger
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskBackupDirDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskSyncStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskRunningTimeDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskSchedulingStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskStateDAO
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AppScope
class SyncTaskRepository @Inject constructor(
    private val syncTaskDAO: SyncTaskDAO,
    private val syncTaskResettingDAO: SyncTaskResettingDAO,
    private val syncTaskStateDAO: SyncTaskStateDAO,
    private val syncTaskRunningTimeDAO: SyncTaskRunningTimeDAO,
    private val syncTaskSchedulingStateDAO: SyncTaskSchedulingStateDAO,
    private val syncTaskExecutionStateDAO: SyncTaskSyncStateDAO,
    private val syncTaskBackupDirDAO: SyncTaskBackupDirDAO,
    private val coroutineScope: CoroutineScope,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher // FIXME: не нравится мне это здесь
)
    : SyncTaskCreatorDeleter,
    SyncTaskResetter,
    SyncTaskReader,
    SyncTaskUpdater,
    SyncTaskStateChanger,
    SyncTaskRunningTimeUpdater,
    SyncTaskMetadataReader
{
    @Deprecated("Сделать возвращаемые значения nullable")
    override suspend fun listSyncTasks(): LiveData<List<SyncTask>> {
//        return syncTaskLocalDataSource.listSyncTasks()
        return syncTaskDAO.list()
    }

    @Deprecated("Сделать возвращаемые значения nullable")
    override suspend fun getSyncTask(id: String): SyncTask {
//        return syncTaskLocalDataSource.getTask(id)
        return syncTaskDAO.get(id)
    }

    override suspend fun getSyncTaskNullable(id: String): SyncTask? {
        return syncTaskDAO.getNullable(id)
    }

    @Deprecated("Сделать возвращаемые значения nullable")
    override suspend fun getSyncTaskAsLiveData(taskId: String): LiveData<SyncTask> {
        return syncTaskDAO.getAsLiveData(taskId)
    }

    override suspend fun createSyncTask(syncTask: SyncTask) {
//        syncTaskLocalDataSource.addTask(syncTask)
        syncTaskDAO.addSuspend(syncTask)
    }

    override suspend fun deleteSyncTask(syncTask: SyncTask) {
//        syncTaskLocalDataSource.delete(syncTask)
        syncTaskDAO.deleteSuspend(syncTask)
    }

    override fun updateSyncTask(syncTask: SyncTask) {
        coroutineScope.launch(coroutineDispatcher) {
            syncTaskDAO.update(syncTask)
        }
    }

    override suspend fun changeSyncTaskEnabled(taskId: String, isEnabled: Boolean) {
        syncTaskStateDAO.setEnabled(taskId, isEnabled)
    }

    override fun setTargetBackupDirName(taskId: String, dirName: String) {
        syncTaskBackupDirDAO.setTargetBackupDir(taskId, dirName)
    }

    override fun setSourceBackupDirName(taskId: String, dirName: String) {
        syncTaskBackupDirDAO.setSourceBackupDir(taskId, dirName)
    }

    override suspend fun setSourceReadingState(taskId: String, state: ExecutionState, errorMsg: String?) {
        syncTaskStateDAO.setSourceReadingState(taskId, state, errorMsg)
    }

    override fun setSourceExecutionBackupDirName(taskId: String, dirName: String) {
        syncTaskBackupDirDAO.setSourceExecutionBackupDir(taskId, dirName)
    }

    override fun setTargetExecutionBackupDirName(taskId: String, dirName: String) {
        syncTaskBackupDirDAO.setTargetExecutionBackupDir(taskId, dirName)
    }

    override suspend fun resetSourceReadingBadState(taskId: String) {
        syncTaskStateDAO.resetSourceReadingBadState(taskId)
    }


    override suspend fun setSuccessState(taskId: String) {
        syncTaskStateDAO.setSyncState(taskId, ExecutionState.SUCCESS)
    }

    override suspend fun setRunningState(taskId: String) {
        syncTaskStateDAO.setSyncState(taskId, ExecutionState.RUNNING)
    }

    override suspend fun setErrorState(taskId: String, exception: Exception) {
        changeExecutionState(taskId, ExecutionState.ERROR, exception.errorMsg)
    }


    override suspend fun changeSchedulingState(taskId: String, newState: ExecutionState, errorMsg: String) {
        changeExecutionState(syncTaskSchedulingStateDAO, taskId, newState, errorMsg)
    }

    override suspend fun changeExecutionState(taskId: String, newState: ExecutionState, errorMsg: String) {
        Log.d(
            TAG,
            "changeExecutionState(), taskId: $taskId, newState: $newState, errorMsg: $errorMsg"
        )
        changeExecutionState(syncTaskExecutionStateDAO, taskId, newState, errorMsg)
    }


    private suspend fun changeExecutionState(syncStateChanger: SyncStateChanger,
                                             taskId: String,
                                             newState: ExecutionState,
                                             errorMsg: String = "") {
        when(newState) {
            ExecutionState.NEVER -> syncStateChanger.setIdleState(taskId)
            ExecutionState.RUNNING -> syncStateChanger.setBusyState(taskId)
            ExecutionState.SUCCESS -> syncStateChanger.setSuccessState(taskId)
            ExecutionState.ERROR -> syncStateChanger.setErrorState(taskId, errorMsg)
        }
    }


    companion object {
        val TAG: String = SyncTaskRepository::class.java.simpleName
    }

    override suspend fun updateStartTime(taskId: String) {
        syncTaskRunningTimeDAO.updateStartTime(taskId, currentTime)
    }

    override suspend fun updateFinishTime(taskId: String) {
        syncTaskRunningTimeDAO.updateFinishTime(taskId, currentTime)
    }

    override suspend fun getAllTasks(): List<SyncTask> {
        return syncTaskDAO.getAllTasks()
    }

    override fun getSyncTaskAsFlow(taskId: String): Flow<SyncTask> {
        return syncTaskDAO.getAsFlow(taskId)
    }

    override suspend fun clearFinishTime(taskId: String) {
        syncTaskRunningTimeDAO.clearFinishTime(taskId)
    }

    override suspend fun resetSyncTask(taskId: String): Result<SyncTask> {
        syncTaskDAO.get(taskId).also { syncTask ->
            return if (syncTask.notRunningNow) {
                try {
                    syncTaskResettingDAO.resetSyncTask(taskId)
                    Result.success(syncTask)
                } catch (t: Throwable) {
                    Result.failure(t)
                }
            } else {
                Result.failure(RuntimeException("Cannot reset running task"))
            }
        }
    }

    suspend fun exists(taskId: String): Boolean {
        return null != getSyncTaskNullable(taskId)
    }

    override suspend fun getSourceBackupsDirName(taskId: String): String? {
        return syncTaskDAO.getSourceBackupsDirName(taskId)
    }

    override suspend fun getTargetBackupsDirName(taskId: String): String? {
        return syncTaskDAO.getTargetBackupsDirName(taskId)
    }

    override suspend fun getSourceExecutionBackupsDirName(taskId: String): String? {
        return syncTaskDAO.getSourceExecutionBackupsDirName(taskId)
    }

    override suspend fun getTargetExecutionBackupsDirName(taskId: String): String? {
        return syncTaskDAO.getTargetExecutionBackupsDirName(taskId)
    }

    override fun getStartingTime(taskId: String): Long? {
        return syncTaskDAO.getStartingTime(taskId)
    }
}

val SyncTask.notRunningNow: Boolean get() = ExecutionState.RUNNING != executionState