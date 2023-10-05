package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskStarter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskStopper
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskUpdater
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils

class StartStopSyncTaskUseCase(
    private val syncTaskReader: iSyncTaskReader,
    private val syncTaskStarter: iSyncTaskStarter,
    private val syncTaskStopper: iSyncTaskStopper,
    private val syncTaskUpdater: iSyncTaskUpdater
) {
    suspend fun startSyncTask(syncTaskId: String) {

        val syncTask: SyncTask = syncTaskReader.getSyncTask(syncTaskId)
            ?: throw Exception("Задание не найдено")

        syncTaskStarter.startSyncTask(syncTask, object : iSyncTaskStarter.Callbacks {
            override fun onSyncTaskStarted() {
//                syncTask.setIsProgress(true);
//                syncTask.setProgressError(null);
//                mSyncTaskUpdater.updateSyncTask(syncTask);
            }

            override fun onSyncTaskStartingError(e: Exception) {
//                syncTask.setIsProgress(false);
//                syncTask.setIsSuccess(false);
//                syncTask.setProgressError(ExceptionUtils.getErrorMessage(e));
//                mSyncTaskUpdater.updateSyncTask(syncTask);
                Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
            }
        })
    }

    fun stopSyncTask(syncTask: SyncTask?) {
        syncTaskStopper.stopSyncTask(syncTask!!, object : iSyncTaskStopper.Callbacks {
            override fun onSyncTaskStopped() {
//                syncTask.setIsProgress(false);
//                syncTask.setIsSuccess(false);
//                syncTask.setProgressError(null);
//                mSyncTaskUpdater.updateSyncTask(syncTask);
            }

            override fun onSyncTaskStoppingError(e: Exception) {
//                syncTask.setIsProgress(false);
//                syncTask.setIsSuccess(false);
//                syncTask.setProgressError(ExceptionUtils.getErrorMessage(e));
//                mSyncTaskUpdater.updateSyncTask(syncTask);
                Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
            }
        })
    }

    companion object {
        private val TAG = StartStopSyncTaskUseCase::class.java.simpleName
    }
}