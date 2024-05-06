package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.executionIntervalNotZero
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isExecutionIntervalCahnged
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

class SchedulingSyncTaskUseCase @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskScheduler: SyncTaskScheduler,
    private val syncTaskUpdater: SyncTaskUpdater,
    private val syncTaskStateChanger: SyncTaskStateChanger
) {
    // TODO: испытать поведение при ошибках

    suspend fun toggleTaskScheduling(taskId: String) {
        syncTaskReader.getSyncTask(taskId).also { syncTask ->
            if (syncTask.isEnabled) unScheduleSyncTask(syncTask)
            else scheduleSyncTask(syncTask)
        }
    }

    suspend fun unScheduleSyncTask(syncTask: SyncTask) {
        try {
            // FIXME: ExecutionState.RUNNING плохо подходит для состояния регистрации задачи в планировщике.
            syncTaskStateChanger.changeSchedulingState(syncTask.id, ExecutionState.RUNNING)
            syncTaskScheduler.unScheduleSyncTask(syncTask)
            syncTaskStateChanger.changeSchedulingState(syncTask.id, ExecutionState.NEVER)
            syncTaskStateChanger.changeSyncTaskEnabled(syncTask.id, false)
        }
        catch (t: Throwable) {
            syncTaskStateChanger.changeSchedulingState(syncTask.id, ExecutionState.ERROR, ExceptionUtils.getErrorMessage(t))
        }
    }

    suspend fun updateTaskSchedule(syncTask: SyncTask) {
        if (syncTask.isEnabled && syncTask.executionIntervalNotZero()) {
            // Метод SyncTaskScheduler.scheduleSyncTask() обновляет задачу,
            // поэтому необязательно предварительно убирать её из планировщика.
            scheduleSyncTask(syncTask)
        }
    }


    private suspend fun scheduleSyncTask(syncTask: SyncTask) {
        try {
            syncTaskStateChanger.changeSchedulingState(syncTask.id, ExecutionState.RUNNING)
            syncTaskScheduler.scheduleSyncTask(syncTask)
            syncTaskStateChanger.changeSchedulingState(syncTask.id, ExecutionState.SUCCESS)
            syncTaskStateChanger.changeSyncTaskEnabled(syncTask.id, true)
        }
        catch (t: Throwable) {
            syncTaskStateChanger.changeSchedulingState(syncTask.id, ExecutionState.ERROR, ExceptionUtils.getErrorMessage(t))
        }
    }


    private fun setSyncTaskEnabledState(syncTask: SyncTask, isEnabled: Boolean) {
        syncTask.isEnabled = isEnabled
        syncTask.schedulingError = null
        syncTaskUpdater.updateSyncTask(syncTask)
    }

    private fun setSyncTaskErrorState(syncTask: SyncTask, error: Throwable) {
        MyLogger.e(TAG, ExceptionUtils.getErrorMessage(error), error)
        syncTask.schedulingError = ExceptionUtils.getErrorMessage(error)
        syncTaskUpdater.updateSyncTask(syncTask)
    }


    companion object {
        private val TAG = SchedulingSyncTaskUseCase::class.java.simpleName
    }
}