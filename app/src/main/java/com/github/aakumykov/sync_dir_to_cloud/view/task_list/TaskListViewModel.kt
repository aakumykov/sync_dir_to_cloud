package com.github.aakumykov.sync_dir_to_cloud.view.task_list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncTaskRepository
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.TaskManagingViewModel
import com.github.aakumykov.sync_dir_to_cloud.workers.SyncTaskStarterStopper
import kotlinx.coroutines.launch

class TaskListViewModel(application: Application) : TaskManagingViewModel(application) {

    private val startStopUseCase: StartStopSyncTaskUseCase

    init {
        val workManager: WorkManager = WorkManager.getInstance(application)

        val syncTaskStarterStopper: SyncTaskStarterStopper = SyncTaskStarterStopper(workManager)

        val syncTaskRepository: SyncTaskRepository = SyncTaskRepository()

        startStopUseCase = StartStopSyncTaskUseCase(
            syncTaskRepository as iSyncTaskReader,
            syncTaskStarterStopper,
            syncTaskStarterStopper,
            syncTaskRepository as iSyncTaskUpdater
        )
    }


    suspend fun getTaskList(): LiveData<List<SyncTask>> = syncTaskManagingUseCase.listSyncTasks()


    fun runTask(id: String) {
        viewModelScope.launch {
            startStopUseCase.startSyncTask(id)
        }
    }
}