package com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_list

import androidx.lifecycle.ViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.domain.use_cases.sync_task.SyncTaskManagingUseCase

class TaskListViewModel : ViewModel() {

    private lateinit var syncTaskManagingUseCase: SyncTaskManagingUseCase

    init {

    }
    
}