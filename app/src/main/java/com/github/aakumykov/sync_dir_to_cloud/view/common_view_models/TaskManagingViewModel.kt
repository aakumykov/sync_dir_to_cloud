package com.github.aakumykov.sync_dir_to_cloud.view.common_view_models

import android.app.Application
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.OpStateViewModel

abstract class TaskManagingViewModel(application: Application) : OpStateViewModel(application) {

    val syncTaskManagingUseCase: SyncTaskManagingUseCase = App.appComponent().getSyncTaskManagingUseCase()
}