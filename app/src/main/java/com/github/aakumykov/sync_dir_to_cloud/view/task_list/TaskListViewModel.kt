package com.github.aakumykov.sync_dir_to_cloud.view.task_list

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.TaskCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SchedulingSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBDeleter
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.PageOpStateViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TaskListViewModel(
    application: Application,
    private val syncTaskManagingUseCase: SyncTaskManagingUseCase,
    private val syncTaskStartStopUseCase: StartStopSyncTaskUseCase,
    private val syncTaskSchedulingUseCase: SchedulingSyncTaskUseCase,
    private val syncTaskNotificator: SyncTaskNotificator,
    private val syncObjectDBDeleter: SyncObjectDBDeleter,
    private val taskCancellationHolder: TaskCancellationHolder,
)
    : PageOpStateViewModel(application)
{
    suspend fun getTaskList(): LiveData<List<SyncTask>> =
        syncTaskManagingUseCase.listSyncTasks()


    fun startStopTask(taskId: String) {
        viewModelScope.launch {
//            syncTaskStartStopUseCase.startStopSyncTask(taskId)

            if (syncTaskStartStopUseCase.isRunning(taskId)) {

                taskCancellationHolder.getScope(taskId)?.also { scope ->
                    scope.cancel(CancellationException("Прервано пользователем"))
                } ?: {
                    Log.e(TAG, "CoroutineScope не найден для задачи '$taskId'")
                }

            } else {
                syncTaskStartStopUseCase.startStopSyncTask(taskId)
            }
        }
    }

    fun changeTaskEnabled(taskId: String) {
        viewModelScope.launch {
            syncTaskSchedulingUseCase.toggleTaskScheduling(taskId)
        }
    }

    // TODO: TaskDeletingUseCase ?
    fun deleteTask(syncTask: SyncTask) {
        viewModelScope.launch {
            syncTaskStartStopUseCase.stopSyncTask(syncTask)
            syncTaskNotificator.hideNotification(syncTask.id, syncTask.notificationId)
            syncTaskSchedulingUseCase.unScheduleSyncTask(syncTask)
            syncTaskManagingUseCase.deleteSyncTask(syncTask)
        }
    }

    fun resetTask(taskId: String) {
        viewModelScope.launch (Dispatchers.IO) {
            syncTaskManagingUseCase.resetSyncTask(taskId)
            syncObjectDBDeleter.deleteAllObjectsForTask(taskId)
        }
    }

    companion object {
        val TAG: String = TaskListViewModel::class.java.simpleName
    }
}