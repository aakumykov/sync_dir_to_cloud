package com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_edit

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.fragments.TaskManagingViewModel

class TaskEditViewModel(application: Application) : TaskManagingViewModel(application) {

    private val currentTaskLiveData: MutableLiveData<SyncTask> = MutableLiveData()

    fun loadTask(id: String) {
        syncTaskManagingUseCase.getSyncTask(id)
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