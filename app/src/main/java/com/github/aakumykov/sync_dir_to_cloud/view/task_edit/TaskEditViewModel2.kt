package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FullSyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.TaskManagingViewModel
import kotlinx.coroutines.launch

class TaskEditViewModel2(application: Application) : TaskManagingViewModel(application) {

    val syncTask: SyncTask = SyncTask()

    // TODO: сообщение об ошибке и OpState (если нужно, ведь задания сохраняются локально = мгновенно)

    fun saveSyncTask() {
        viewModelScope.launch {
            syncTaskManagingUseCase.createOrUpdateSyncTask(syncTask)
//            val fullSyncTask: FullSyncTask = FullSyncTask(fullSyncTask)
        }
    }
}