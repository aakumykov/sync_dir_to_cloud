package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager.SyncTaskStarterStopper
import javax.inject.Inject

class StartStopSyncTaskUseCase @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStarterStopper: SyncTaskStarterStopper,
    private val syncTaskUpdater: SyncTaskUpdater
) {
    suspend fun startSyncTask(syncTaskId: String) {

        // TODO: кинуть исключение просто так

        val syncTask: SyncTask = syncTaskReader.getSyncTask(syncTaskId)
            ?: throw Exception("Задание не найдено")

        /*syncTaskStarterStopper.startSyncTask(syncTask, object : SyncTaskStarterStopper.Callbacks {
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
        })*/
    }

    fun stopSyncTask(syncTask: SyncTask?) {
        /*syncTaskStopper.stopSyncTask(syncTask!!, object : SyncTaskStopper.Callbacks {
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
        })*/
    }

    companion object {
        private val TAG = StartStopSyncTaskUseCase::class.java.simpleName
    }
}