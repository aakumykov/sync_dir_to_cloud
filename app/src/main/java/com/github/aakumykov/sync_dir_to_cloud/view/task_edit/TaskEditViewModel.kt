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

    private lateinit var currentTask: SyncTask
    private val currentTaskMutableLiveData: MutableLiveData<SyncTask> = MutableLiveData()


    fun prepare(id: String?) {
        viewModelScope.launch {
            if (null == id)
                currentTask = SyncTask()
            else
                currentTask = taskManagingUseCase.getSyncTask(id)

            currentTaskMutableLiveData.postValue(currentTask)
        }
    }

    fun getCurrentTask(): LiveData<SyncTask> {
        return currentTaskMutableLiveData
    }

    fun createOrSaveSyncTask(
        sourcePath: String,
        targetPath: String,
        intervalHours: Int,
        intervalMinutes: Int
    ) {
        currentTask.sourcePath = sourcePath
        currentTask.targetPath = targetPath
        currentTask.intervalHours = intervalHours
        currentTask.intervalMinutes = intervalMinutes

        viewModelScope.launch {
            setOpState(OpState.Busy(TextMessage(R.string.saving_new_task)))
            taskManagingUseCase.createOrUpdateSyncTask(currentTask)
            setOpState(OpState.Success(TextMessage(R.string.task_saved)))
        }
    }
}