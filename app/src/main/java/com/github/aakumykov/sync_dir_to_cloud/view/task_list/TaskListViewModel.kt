package com.github.aakumykov.sync_dir_to_cloud.view.task_list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncTaskRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.data_sources.SyncTaskLocalDataSource
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.TaskManagingViewModel
import com.github.aakumykov.sync_dir_to_cloud.workers.SyncTaskStarterStopper
import kotlinx.coroutines.launch

class TaskListViewModel(application: Application) : TaskManagingViewModel(application) {

    private val startStopUseCase: StartStopSyncTaskUseCase

    init {
        val workManager: WorkManager = WorkManager.getInstance(application)

        val syncTaskStarterStopper = SyncTaskStarterStopper(workManager)

        val syncTaskDAO = App.getAppDatabase(application).getSyncTaskDAO()

        val syncTaskLocalDataSource = SyncTaskLocalDataSource(syncTaskDAO)

        val syncTaskRepository = SyncTaskRepository(syncTaskLocalDataSource)

        startStopUseCase = StartStopSyncTaskUseCase(
            syncTaskRepository as SyncTaskReader,
            syncTaskStarterStopper,
            syncTaskStarterStopper,
            syncTaskRepository as SyncTaskUpdater
        )
    }


    suspend fun getTaskList(): LiveData<List<SyncTask>> = syncTaskManagingUseCase.listSyncTasks()


    fun runTask(id: String) {
        viewModelScope.launch {
            startStopUseCase.startSyncTask(id)
        }
    }
}