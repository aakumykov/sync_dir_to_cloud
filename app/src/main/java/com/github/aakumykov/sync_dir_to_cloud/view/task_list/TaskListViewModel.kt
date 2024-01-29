package com.github.aakumykov.sync_dir_to_cloud.view.task_list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SchedulingSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.TaskManagingViewModel
import kotlinx.coroutines.launch

// TODO: поля в конструктор, LAZY
class TaskListViewModel(application: Application) : TaskManagingViewModel(application) {

    private val syncTaskStartStopUseCase: StartStopSyncTaskUseCase = App.getAppComponent().getStartStopSyncTaskUseCase()
    private val syncTaskSchedulingUseCase: SchedulingSyncTaskUseCase = App.getAppComponent().getTaskSchedulingUseCase()


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

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            syncTaskStartStopUseCase.stopSyncTask(taskId)
            syncTaskSchedulingUseCase.unScheduleSyncTask(taskId)
            syncTaskManagingUseCase.deleteSyncTask(taskId)
        }
    }

    @Deprecated("Временный, затем перейти на startStopTask()...")
    fun startTask(taskId: String) {
        viewModelScope.launch {
            syncTaskStartStopUseCase.startSyncTask(taskId)
        }
    }

    @Deprecated("Временный, затем перейти на startStopTask()...")
    fun stopTask(taskId: String) {
        viewModelScope.launch {
            syncTaskStartStopUseCase.stopSyncTask(taskId)
        }
    }
}