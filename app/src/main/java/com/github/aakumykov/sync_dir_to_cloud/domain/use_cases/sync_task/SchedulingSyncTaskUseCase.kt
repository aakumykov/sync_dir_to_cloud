package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler.ScheduleCallbacks
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler.UnScheduleCallbacks
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

class SchedulingSyncTaskUseCase @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskScheduler: SyncTaskScheduler,
    private val syncTaskUpdater: SyncTaskUpdater
) {
    // TODO: испытать поведение при ошибках

    private fun scheduleSyncTask(syncTask: SyncTask) {
        syncTaskScheduler.scheduleSyncTask(syncTask, object : ScheduleCallbacks {

            override fun onSyncTaskScheduleSuccess() {
                setSyncTaskEnabledState(syncTask, true)
            }

            override fun onSyncTaskScheduleError(error: Throwable) {
                setSyncTaskErrorState(syncTask, error)
            }
        })
    }

    private fun unScheduleSyncTask(syncTask: SyncTask) {

        syncTaskScheduler.unScheduleSyncTask(syncTask, object : UnScheduleCallbacks {

            override fun onSyncTaskUnScheduleSuccess() {
                setSyncTaskEnabledState(syncTask, false)
            }

            override fun onSyncTaskUnScheduleError(error: Throwable) {
                setSyncTaskErrorState(syncTask, error)
            }
        })
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

    suspend fun toggleTaskScheduling(taskId: String) {
        syncTaskReader.getSyncTask(taskId).also {
            if (it.isEnabled) unScheduleSyncTask(it)
            else scheduleSyncTask(it)
        }
    }

    companion object {
        private val TAG = SchedulingSyncTaskUseCase::class.java.simpleName
    }
}