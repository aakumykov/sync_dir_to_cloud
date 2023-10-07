package com.github.aakumykov.sync_dir_to_cloud.view.task_list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.TaskManagingViewModel
import kotlinx.coroutines.launch

class TaskListViewModel(application: Application) : TaskManagingViewModel(application) {

    private val startStopUseCase: StartStopSyncTaskUseCase = App.appComponent().getStartStopSyncTaskUseCase()


    suspend fun getTaskList(): LiveData<List<SyncTask>> =
        syncTaskManagingUseCase.listSyncTasks()


    fun runTask(id: String) {
        viewModelScope.launch {
            startStopUseCase.startSyncTask(id)
        }
    }
}