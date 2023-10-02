package com.github.aakumykov.sync_dir_to_cloud.view.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskManager
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.sync_task.SyncTaskRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.sync_task.data_sources.SyncTaskLocalDataSource

abstract class TaskManagingViewModel(application: Application) : AndroidViewModel(application) {

    private var _syncTaskManagingUseCase: SyncTaskManagingUseCase
    protected val syncTaskManagingUseCase get() = _syncTaskManagingUseCase

    init {
        val syncTaskDAO = (getApplication() as App).appDatabase.getSyncTaskDAO()
        val syncTaskLocalDataSource = SyncTaskLocalDataSource(syncTaskDAO)
        val syncTaskRepository = SyncTaskRepository(syncTaskLocalDataSource)

        _syncTaskManagingUseCase =
            SyncTaskManagingUseCase(
                syncTaskRepository as iSyncTaskManager,
                syncTaskRepository as iSyncTaskUpdater
            )
    }
}