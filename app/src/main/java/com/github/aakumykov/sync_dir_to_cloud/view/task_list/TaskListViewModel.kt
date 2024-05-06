package com.github.aakumykov.sync_dir_to_cloud.view.task_list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SchedulingSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.PageOpStateViewModel
import kotlinx.coroutines.launch

class TaskListViewModel(
    application: Application,
    private val syncTaskManagingUseCase: SyncTaskManagingUseCase,
    private val syncTaskStartStopUseCase: StartStopSyncTaskUseCase,
    private val syncTaskSchedulingUseCase: SchedulingSyncTaskUseCase,
    private val syncTaskNotificator: SyncTaskNotificator
)
    : PageOpStateViewModel(application)
{
    suspend fun getTaskList(): LiveData<List<SyncTask>> =
        syncTaskManagingUseCase.listSyncTasks()


    fun startStopTask(taskId: String) {
        viewModelScope.launch {
            syncTaskStartStopUseCase.startStopSyncTask(taskId)
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
}