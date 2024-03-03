package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.TaskManagingViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.OpState
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import kotlinx.coroutines.launch

class TaskEditViewModel(application: Application) : TaskManagingViewModel(application) {


    //
    // LiveData для оповещения View о том, что можно отображать данные.
    //
    private val _syncTaskMutableLiveData: MutableLiveData<SyncTask> = MutableLiveData()
    val syncTaskLiveData: LiveData<SyncTask> = _syncTaskMutableLiveData

    //
    // Объект из этого поля выступает в качестве временного хранилища редактируемых данных
    // (чтобы при сохранении не передавать их через аргументы метода, а просто брать объект из этого поля).
    //
    val currentTask get(): SyncTask? = _syncTaskMutableLiveData.value


    private val cloudAuthManagingUseCase = App.getAppComponent().getCloudAuthManagingUseCase()


    //
    // LiveData с объектом CloudAuth, который связан с текущим
    //
    private val _cloudAuthMutableLiveData: MutableLiveData<CloudAuth> = MutableLiveData()
    val cloudAuthLiveData: LiveData<CloudAuth> get() = _cloudAuthMutableLiveData

    //
    // Поле для более удобного обращения к текущему CloudAuth (возможно, понадобится "var").
    //
    val currentCloudAuth get(): CloudAuth? = _cloudAuthMutableLiveData.value



    fun saveSyncTask() {
        viewModelScope.launch {
            currentTask?.let {
                setOpState(OpState.Busy(TextMessage(R.string.saving_new_task)))
                syncTaskManagingUseCase.createOrUpdateSyncTask(it)
                setOpState(OpState.Success(TextMessage(R.string.task_saved)))
            }
        }
    }


    fun prepareForEdit(taskId: String) {
        viewModelScope.launch {
            _syncTaskMutableLiveData.value = syncTaskManagingUseCase.getSyncTask(taskId)

            loadCloudAuth(currentTask?.cloudAuthId)
        }
    }


    fun prepareForCreate() {
        val newSyncTask = SyncTask()
        _syncTaskMutableLiveData.value = newSyncTask
    }


    private fun loadCloudAuth(cloudAuthId: String?) {
        viewModelScope.launch {
            cloudAuthId?.let {
                _cloudAuthMutableLiveData.value = cloudAuthManagingUseCase.getCloudAuth(it)
            }
        }
    }
}