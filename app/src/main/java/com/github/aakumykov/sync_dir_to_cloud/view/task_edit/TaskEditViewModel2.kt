package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.TaskManagingViewModel
import kotlinx.coroutines.launch

class TaskEditViewModel2(application: Application) : TaskManagingViewModel(application) {

    private var _syncTask: SyncTask? = null
    val syncTask get(): SyncTask? = _syncTask

    // TODO: сообщение об ошибке и OpState (если нужно, ведь задания сохраняются локально = мгновенно)

    fun saveSyncTask() {
        viewModelScope.launch {
//            syncTaskManagingUseCase.createOrUpdateSyncTask(syncTask)
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