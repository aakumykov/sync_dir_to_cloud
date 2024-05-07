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

    suspend fun toggleTaskScheduling(taskId: String): Result<String> {
        return syncTaskReader.getSyncTask(taskId).let { syncTask ->
            if (syncTask.isEnabled) unScheduleSyncTask(syncTask)
            else scheduleSyncTask(syncTask)
        }
    }

    /**
     * @return Result с taskId внутри.
     */
    // TODO: возвращать сообщение об ошибке как TextMessage
    private suspend fun scheduleSyncTask(syncTask: SyncTask): Result<String> {
        return try {
            if (syncTask.executionIntervalNotZero()) {
                syncTaskStateChanger.changeSchedulingState(syncTask.id, ExecutionState.RUNNING)
                syncTaskScheduler.scheduleSyncTask(syncTask)
                syncTaskStateChanger.changeSchedulingState(syncTask.id, ExecutionState.SUCCESS)
                syncTaskStateChanger.changeSyncTaskEnabled(syncTask.id, true)
                Result.success(syncTask.id)
            } else {
                throw Exception("Scheduling interval cannot be zero.")
            }
        }
        catch (t: Throwable) {
            markTaskAsSchedulingError(syncTask.id, t)
            Result.failure(t)
        }
    }

    /**
     * @return Result с taskId внутри.
     */
    suspend fun unScheduleSyncTask(syncTask: SyncTask): Result<String> {
        return try {
            // FIXME: ExecutionState.RUNNING плохо подходит для состояния регистрации задачи в планировщике.
            syncTaskStateChanger.changeSchedulingState(syncTask.id, ExecutionState.RUNNING)
            syncTaskScheduler.unScheduleSyncTask(syncTask)
            syncTaskStateChanger.changeSchedulingState(syncTask.id, ExecutionState.NEVER)
            syncTaskStateChanger.changeSyncTaskEnabled(syncTask.id, false)
            Result.success(syncTask.id)
        }
        catch (t: Throwable) {
            markTaskAsSchedulingError(syncTask.id, t)
            Result.failure(t)
        }
    }

    private suspend fun markTaskAsSchedulingError(taskId: String, t: Throwable) {
        syncTaskStateChanger.changeSchedulingState(taskId, ExecutionState.ERROR, ExceptionUtils.getErrorMessage(t))
    }


    suspend fun updateTaskSchedule(syncTask: SyncTask): Result<String> {
        if (syncTask.isEnabled) {
            // Метод SyncTaskScheduler.scheduleSyncTask() обновляет задачу,
            // поэтому необязательно предварительно убирать её из планировщика.
            scheduleSyncTask(syncTask)
        }
        return Result.success(syncTask.id)
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