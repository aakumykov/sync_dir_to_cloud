package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTaskBase
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.TaskManagingViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.OpState
import com.github.aakumykov.sync_dir_to_cloud.view.utils.TextMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TaskEditViewModel(application: Application) : TaskManagingViewModel(application) {

    private var currentTask: SyncTask? = null
    private val currentTaskMutableLiveData: MutableLiveData<SyncTask> = MutableLiveData()


    fun prepareForNewTask() {

    }

    fun loadTask(id: String) {
        viewModelScope.launch {
            val syncTask = syncTaskManagingUseCase.getSyncTask(id)
            if (null != syncTask)
                currentTaskMutableLiveData.postValue(syncTask)
        }
    }

    fun getCurrentTask(): LiveData<SyncTask> {
        return currentTaskMutableLiveData
    }

    fun createOrSaveSyncTask(syncTaskBase: SyncTaskBase) {
        // FIXME: загрузка currentTask происходит асинхронно, это условие ненадёжно
        if (null == currentTask)
            createNewTask(syncTaskBase)
        else
            updateExistingTask(syncTaskBase)
    }

    private fun createNewTask(syncTaskBase: SyncTaskBase) {

        val syncTask = SyncTask(syncTaskBase)

        setOpState(OpState.Busy(TextMessage(R.string.creating_new_task)))

        viewModelScope.launch(Dispatchers.IO) {
            syncTaskManagingUseCase.addSyncTask(syncTask)
            delay(1000)
        }

        setOpState(OpState.Success)
    }

    private fun updateExistingTask(syncTaskBase: SyncTaskBase) {
        TODO("Not yet implemented")
    }
}