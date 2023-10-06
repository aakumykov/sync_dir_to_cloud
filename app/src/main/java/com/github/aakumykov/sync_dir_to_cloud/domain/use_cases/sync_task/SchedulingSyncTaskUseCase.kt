package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler.ScheduleCallbacks
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskScheduler.UnScheduleCallbacks
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

class SchedulingSyncTaskUseCase @Inject constructor(
    private val syncTaskScheduler: SyncTaskScheduler,
    private val syncTaskUpdater: SyncTaskUpdater
) {
    fun scheduleSyncTask(syncTask: SyncTask?) {
        syncTaskScheduler.scheduleSyncTask(syncTask, object : ScheduleCallbacks {
            override fun onSyncTaskScheduleSuccess() {
//                syncTask.setScheduled(true);
//                syncTask.setSchedulingError(null);
//                mSyncTaskUpdater.updateSyncTask(syncTask);
            }

            override fun onSyncTaskScheduleError(e: Exception) {
//                syncTask.setScheduled(false);
//                syncTask.setSchedulingError(ExceptionUtils.getErrorMessage(e));
//                mSyncTaskUpdater.updateSyncTask(syncTask);
                Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
            }
        })
    }

    fun unScheduleSyncTask(syncTask: SyncTask?) {
        syncTaskScheduler.unScheduleSyncTask(syncTask, object : UnScheduleCallbacks {
            override fun onSyncTaskUnScheduleSuccess() {
//                syncTask.setScheduled(false);
//                syncTask.setSchedulingError(null);
//                mSyncTaskUpdater.updateSyncTask(syncTask);
            }

            override fun onSyncTaskUnScheduleError(e: Exception) {
//                syncTask.setScheduled(true);
//                syncTask.setSchedulingError(ExceptionUtils.getErrorMessage(e));
//                mSyncTaskUpdater.updateSyncTask(syncTask);
                Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
            }
        })
    }

    companion object {
        private val TAG = SchedulingSyncTaskUseCase::class.java.simpleName
    }
}