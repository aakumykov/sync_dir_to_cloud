package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.TaskManagingViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.OpState
import com.github.aakumykov.sync_dir_to_cloud.view.view_utils.TextMessage
import kotlinx.coroutines.launch

class TaskEditViewModel2(application: Application) : TaskManagingViewModel(application) {

    private var _syncTask: SyncTask? = null
    val syncTask get(): SyncTask? = _syncTask


    fun saveSyncTask() {
        viewModelScope.launch {
            syncTask?.let {
                setOpState(OpState.Busy(TextMessage(R.string.saving_new_task)))
                syncTaskManagingUseCase.createOrUpdateSyncTask(it)
                setOpState(OpState.Success(TextMessage(R.string.task_saved)))
            }
        }
    }


    fun prepareForEdit(taskId: String) {
        viewModelScope.launch {
            _syncTask = syncTaskManagingUseCase.getSyncTask(taskId)
        }
    }


    fun prepareForCreate() {
        _syncTask = SyncTask()
    }
}