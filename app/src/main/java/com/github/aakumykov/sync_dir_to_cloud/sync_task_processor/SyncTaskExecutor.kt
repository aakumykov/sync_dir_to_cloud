package com.github.aakumykov.sync_dir_to_cloud.sync_task_processor

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionLogItemType
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.extensions.classNameWithHash
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.extensions.tag
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskRunningTimeUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log.TaskLogger
import com.github.aakumykov.sync_dir_to_cloud.loggers2.task_logger.TaskLogger2
import com.github.aakumykov.sync_dir_to_cloud.loggers2.task_logger.TaskLogger2AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskProcessor.Companion.TAG
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskProcessorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.workers.TaskJobsHolder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Задача класса - запускать выполнение задачи и журналировать это выполнение.
 */
// TODO: поменять именами Executor и Processor ...
class SyncTaskExecutor @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val taskLogger: TaskLogger,
    private val taskLogger2AssistedFactory: TaskLogger2AssistedFactory,
    private val executionLogger: ExecutionLogger,
    private val syncTaskProcessorFactory: SyncTaskProcessorAssistedFactory,
    private val resources: Resources,
){
    private val syncTaskRunningTimeUpdater: SyncTaskRunningTimeUpdater by lazy { appComponent.getSyncTaskRunningTimeUpdater() }

    private val executionId: String get() = hashCode().toString()

    private val taskLogger2: TaskLogger2 by lazy { taskLogger2AssistedFactory.create(executionId) }


    suspend fun executeSyncTask(scope: CoroutineScope, taskId: String) {
        Log.d(TAG, "executeSyncTask() called with: scope = $scope, taskId = $taskId")
        scope.launch (Dispatchers.IO) {
            Log.d(TAG, "А")
            executeSyncTaskReal(this, taskId)
            Log.d(TAG, "Б")
        }.join()
    }


    private suspend fun executeSyncTaskReal(scope: CoroutineScope, taskId: String) {

        Log.d(tag, "========= executeSyncTaskReal(taskId: $taskId, executionId: $executionId) [hashCode:${hashCode()}] СТАРТ ========")

        val syncTask = syncTaskReader.getSyncTask(taskId)
        val taskId = syncTask.id

        try {
            logExecutionStart(taskId)
            taskLogger2.logTaskStarted(syncTask)
            syncTaskRunningTimeUpdater.updateStartTime(taskId)

            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.RUNNING)

            syncTaskProcessorFactory
                .create(syncTask, executionId, scope)
                .processSyncTask()

            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.SUCCESS)
            taskLogger2.logTaskFinished(syncTask)
        }
        catch (e: CancellationException) {
            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.CANCELLED)
            Log.i(TAG, "Задача $taskId отменена: ${e.errorMsg}")
        }
        catch (t: Throwable) {
            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.ERROR, t.errorMsg)
            taskLogger2.logTaskError(syncTask, t)
            Log.e(TAG, t.errorMsg, t)
        }
        finally {
            // TODO: ошибочное расположение
            syncTaskRunningTimeUpdater.updateFinishTime(taskId)
            logExecutionFinish(taskId)
        }

        Log.d(tag, "========= executeSyncTaskReal() [${classNameWithHash()}] ФИНИШ ========")
    }


    private suspend fun logExecutionStart(taskId: String) {

        executionLogger.log(TaskExecutionLogItem.createStartingItem(
            taskId = taskId,
            executionId = executionId,
            message = resources.getString(R.string.EXECUTION_LOG_work_begins)
        ))

        taskLogger.logRunning(TaskLogEntry(
            executionId = hashCode().toString(),
            taskId = taskId,
            entryType = ExecutionLogItemType.START
        ))
    }


    private suspend fun logExecutionFinish(taskId: String) {

        executionLogger.log(TaskExecutionLogItem.createFinishingItem(
            taskId = taskId,
            executionId = executionId,
            message = resources.getString(R.string.EXECUTION_LOG_work_ends)
        ))

        taskLogger.logSuccess(TaskLogEntry(
            executionId = hashCode().toString(),
            taskId = taskId,
            entryType = ExecutionLogItemType.FINISH
        ))
    }


    private suspend fun logExecutionError(syncTask: SyncTask, t: Throwable) {

        executionLogger.log(TaskExecutionLogItem.createErrorItem(
            taskId = syncTask.id,
            executionId = executionId,
            message = resources.getString(R.string.EXECUTION_LOG_work_error),
            details = t.errorMsg
        ))

        taskLogger.logError(TaskLogEntry(
            executionId = hashCode().toString(),
            taskId = syncTask.id,
            entryType = ExecutionLogItemType.ERROR,
            errorMsg = null
        ))
    }

    companion object {
        val TAG: String = SyncTaskExecutor::class.java.simpleName
    }
}