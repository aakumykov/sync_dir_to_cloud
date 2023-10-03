package com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_edit

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.fragments.TaskManagingViewModel
import kotlinx.coroutines.launch

class TaskEditViewModel(application: Application) : TaskManagingViewModel(application) {

    private val currentTaskMutableLiveData: MutableLiveData<SyncTask> = MutableLiveData()

    fun loadTask(id: String) {
        viewModelScope.launch {
            val syncTask = syncTaskManagingUseCase.getSyncTask(id)
            currentTaskMutableLiveData.postValue(syncTask)
        }
    }

    fun getCurrentTask(): LiveData<SyncTask> {
        return currentTaskMutableLiveData
    }

    fun onSaveButtonClicked(syncTask: SyncTask) {
        if (null == syncTask.id)
            createNewTask()
        else
            updateExistingTask()
    }

    private fun createNewTask() {
        TODO("Not yet implemented")
    }

    private fun updateExistingTask() {
        TODO("Not yet implemented")
    }
}