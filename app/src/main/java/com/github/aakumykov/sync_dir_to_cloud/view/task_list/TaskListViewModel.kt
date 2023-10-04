package com.github.aakumykov.sync_dir_to_cloud.view.task_list

import android.app.Application
import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.TaskManagingViewModel

class TaskListViewModel(application: Application) : TaskManagingViewModel(application) {

    suspend fun getTaskList(): LiveData<List<SyncTask>> = syncTaskManagingUseCase.listSyncTasks()
}