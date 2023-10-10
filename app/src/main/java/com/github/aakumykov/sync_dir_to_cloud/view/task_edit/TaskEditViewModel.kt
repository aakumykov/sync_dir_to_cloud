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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TaskEditViewModel(application: Application) : TaskManagingViewModel(application) {

    private var currentTask: SyncTask? = null
    private val currentTaskMutableLiveData: MutableLiveData<SyncTask> = MutableLiveData()


    fun loadTask(id: String) {
        viewModelScope.launch {
            currentTask = syncTaskManagingUseCase.getSyncTask(id)
            if (null != currentTask)
                currentTaskMutableLiveData.postValue(currentTask!!)
        }
    }

    fun getCurrentTask(): LiveData<SyncTask> {
        return currentTaskMutableLiveData
    }

    // FIXME: загрузка currentTask происходит асинхронно, это условие ненадёжно
    fun createOrSaveSyncTask(sourcePath: String, targetPath: String) {
        if (null == currentTask)
            createNewTask(sourcePath, targetPath)
        else
            updateExistingTask(sourcePath, targetPath)
    }

    private fun createNewTask(sourcePath: String, targetPath: String) {

        val syncTask = SyncTask(sourcePath, targetPath)

        setOpState(OpState.Busy(TextMessage(R.string.creating_new_task)))

        viewModelScope.launch(Dispatchers.IO) {
            syncTaskManagingUseCase.addSyncTask(syncTask)
            delay(1000)
        }

        setOpState(OpState.Success(TextMessage(R.string.sync_task_created)))
    }

    private fun updateExistingTask(sourcePath: String, targetPath: String) {

        currentTask?.let {
            it.sourcePath = sourcePath
            it.targetPath = targetPath

            setOpState(OpState.Busy(TextMessage(R.string.updating_task)))

            viewModelScope.launch(Dispatchers.IO) {
                syncTaskManagingUseCase.updateSyncTask(it)
            }

            setOpState(OpState.Success(TextMessage(R.string.sync_task_updated)))
        }
    }

    fun createOrSaveSyncTask2() {

    }

    fun storeCurrentTask(syncTask: SyncTask) {
        currentTask = syncTask
    }
}