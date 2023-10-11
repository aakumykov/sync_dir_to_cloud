package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler.ScheduleCallbacks
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler.UnScheduleCallbacks
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

class SchedulingSyncTaskUseCase @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskScheduler: SyncTaskScheduler,
    private val syncTaskUpdater: SyncTaskUpdater
) {
    // TODO: испытать поведение при ошибках

    suspend fun scheduleSyncTask(taskId: String) {

        val syncTask = syncTaskReader.getSyncTask(taskId)

        syncTaskScheduler.scheduleSyncTask(syncTask, object : ScheduleCallbacks {

            override fun onSyncTaskScheduleSuccess() {
                syncTask.isEnabled = true
                syncTaskUpdater.updateSyncTask(syncTask)
            }

            override fun onSyncTaskScheduleError(error: Throwable) {
                Log.e(TAG, ExceptionUtils.getErrorMessage(error), error)
            }
        })
    }

    suspend fun unScheduleSyncTask(taskId: String) {

        val syncTask = syncTaskReader.getSyncTask(taskId)

        syncTaskScheduler.unScheduleSyncTask(syncTask, object : UnScheduleCallbacks {

            override fun onSyncTaskUnScheduleSuccess() {
                syncTask.isEnabled = false
                syncTaskUpdater.updateSyncTask(syncTask)
            }

            override fun onSyncTaskUnScheduleError(error: Throwable) {
                Log.e(TAG, ExceptionUtils.getErrorMessage(error), error)
            }
        })
    }

    companion object {
        private val TAG = SchedulingSyncTaskUseCase::class.java.simpleName
    }
}