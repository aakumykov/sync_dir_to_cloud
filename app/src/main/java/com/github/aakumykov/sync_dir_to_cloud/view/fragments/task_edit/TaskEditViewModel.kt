package com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_edit

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTaskBase
import com.github.aakumykov.sync_dir_to_cloud.view.fragments.TaskManagingViewModel
import kotlinx.coroutines.launch

class TaskEditViewModel(application: Application) : TaskManagingViewModel(application) {

    private var currentTask: SyncTask? = null
    private val currentTaskMutableLiveData: MutableLiveData<SyncTask> = MutableLiveData()


    fun prepareForNewTask() {

    }

    fun loadTask(id: String) {
        viewModelScope.launch {
            val syncTask = syncTaskManagingUseCase.getSyncTask(id)
            currentTaskMutableLiveData.postValue(syncTask)
        }
    }

    fun getCurrentTask(): LiveData<SyncTask> {
        return currentTaskMutableLiveData
    }

    fun onSaveButtonClicked(syncTaskBase: SyncTaskBase) {
        if (null == currentTask)
            createNewTask(syncTaskBase)
        else
            updateExistingTask(syncTaskBase)
    }

    private fun createNewTask(syncTaskBase: SyncTaskBase) {
        TODO("Not yet implemented")
    }

    private fun updateExistingTask(syncTaskBase: SyncTaskBase) {
        TODO("Not yet implemented")
    }
}