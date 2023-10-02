package com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskManager
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.sync_task.SyncTaskRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.sync_task.data_sources.SyncTaskLocalDataSource

class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    private var syncTaskManagingUseCase: SyncTaskManagingUseCase

    init {
        val syncTaskDAO = (getApplication() as App).appDatabase.getSyncTaskDAO()

        val syncTaskLocalDataSource = SyncTaskLocalDataSource(syncTaskDAO)

        val syncTaskRepository: SyncTaskRepository = SyncTaskRepository(syncTaskLocalDataSource)

        syncTaskManagingUseCase =
            SyncTaskManagingUseCase(
                syncTaskRepository as iSyncTaskManager,
                syncTaskRepository as iSyncTaskUpdater
            )
    }

    fun getTaskList(): LiveData<List<SyncTask>> = syncTaskManagingUseCase.listSyncTasks()

}