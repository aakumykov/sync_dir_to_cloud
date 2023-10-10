package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.TaskManagingViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.OpState
import com.github.aakumykov.sync_dir_to_cloud.view.view_utils.TextMessage
import kotlinx.coroutines.launch

class TaskEditViewModel(application: Application) : TaskManagingViewModel(application) {

    private var currentTask: SyncTask? = null
    private val currentTaskMutableLiveData: MutableLiveData<SyncTask> = MutableLiveData()


    fun loadTask(id: String) {
        viewModelScope.launch {
            syncTaskManagingUseCase.getSyncTask(id)?.let {
                currentTask = it
                currentTaskMutableLiveData.postValue(it)
            }
        }
    }

    fun getCurrentTask(): LiveData<SyncTask> {
        return currentTaskMutableLiveData
    }

    fun createOrSaveSyncTask2(syncTask: SyncTask) {
        viewModelScope.launch {
            setOpState(OpState.Busy(TextMessage(R.string.saving_new_task)))
            syncTaskManagingUseCase.createOrUpdateSyncTask(syncTask)
            setOpState(OpState.Success(TextMessage(R.string.task_saved)))
        }
    }
}