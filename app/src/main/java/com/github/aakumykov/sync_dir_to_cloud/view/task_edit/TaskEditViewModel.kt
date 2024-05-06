package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isExecutionIntervalCahnged
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SchedulingSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.OpState
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.PageOpStateViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import kotlinx.coroutines.launch

// TODO: отправка во View Toast-сообщений
class TaskEditViewModel(
    application: Application,
    private val syncTaskManagingUseCase: SyncTaskManagingUseCase,
    private val syncTaskSchedulingUseCase: SchedulingSyncTaskUseCase,
    private val cloudAuthReader: CloudAuthReader
)
    : PageOpStateViewModel(application)
{
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


    fun saveSyncTask() {
        viewModelScope.launch {
            currentTask?.let { syncTask ->
                setOpState(OpState.Busy(TextMessage(R.string.saving_new_task)))

                // FIXME: что, если ошибка?
                syncTaskManagingUseCase.createOrUpdateSyncTask(syncTask)

                syncTaskSchedulingUseCase.updateTaskSchedule(syncTask)

                setOpState(OpState.Success(TextMessage(R.string.task_saved)))
            }
        }
    }


    fun prepareForEdit(taskId: String) {
        viewModelScope.launch {
            _syncTaskMutableLiveData.value = syncTaskManagingUseCase.getSyncTask(taskId)
        }
    }


    fun prepareForCreate() {
        val newSyncTask = SyncTask()
        _syncTaskMutableLiveData.value = newSyncTask
    }


    suspend fun getCloudAuth(authId: String?): CloudAuth? {
        return authId?.let { cloudAuthReader.getCloudAuth(authId) }
    }

    fun setSourceAuthAndType(cloudAuth: CloudAuth) {
        currentTask?.apply {
            sourceAuthId = cloudAuth.id
            sourceStorageType = cloudAuth.storageType
        }
        reSendCurrentTask()
    }

    fun setTargetAuthAndType(cloudAuth: CloudAuth) {
        currentTask?.apply {
            targetAuthId = cloudAuth.id
            targetStorageType = cloudAuth.storageType
        }
        reSendCurrentTask()
    }

    private fun reSendCurrentTask() {
        _syncTaskMutableLiveData.value = currentTask
    }

    fun setIntervalHours(hoursCount: Int) {
        currentTask?.apply {
            oldIntervalH = intervalHours
            intervalHours = hoursCount
        }
    }

    fun setIntervalMinutes(minutesCount: Int) {
        currentTask?.apply {
            oldIntervalM = intervalMinutes
            intervalMinutes = minutesCount
        }
    }
}