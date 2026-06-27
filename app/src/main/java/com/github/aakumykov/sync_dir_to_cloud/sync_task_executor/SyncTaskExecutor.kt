package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskRunningTimeUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.job_holdes.TaskJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.loggers2.task_logger.TaskLogger
import com.github.aakumykov.sync_dir_to_cloud.loggers2.task_logger.TaskLoggerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.sync_task_processor.SyncTaskProcessorAssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

/**
 * Задача класса - запускать выполнение задачи и журналировать это выполнение.
 * Он ловит все ошибки выполнения Задачи и регистрирует их как "ошибки задачи".
 *
 * Вообще, в соответствие объектам программы классы журналов должны быть:
 * TaskLogItem
 * InstructionLogItem
 */
// TODO: поменять именами Executor и Processor ...
// TODO: передавать в @AssistedInject taskId, чтобы получать SyncTask как свойство...
class SyncTaskExecutor @AssistedInject constructor(
    @Assisted private val taskId: String,
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val taskLoggerAssistedFactory: TaskLoggerAssistedFactory,
    private val syncTaskProcessorFactory: SyncTaskProcessorAssistedFactory,
//    private val syncTaskNotificator: SyncTaskNotificator
){
    private val executionId: String by lazy { hashCode().toString() }
    private val logItemId: String by lazy { newRandomId }

    private val taskLogger: TaskLogger by lazy { taskLoggerAssistedFactory.create(taskId, executionId) }
    private val syncTaskRunningTimeUpdater: SyncTaskRunningTimeUpdater by lazy { appComponent.getSyncTaskRunningTimeUpdater() }


    // TODO: разобраться с этим "parent scope": что он и зачем именно родительский.
    suspend fun executeSyncTask(parentScope: CoroutineScope, taskId: String) {
        try {
            Log.d(TAG, "executeSyncTask() called with: scope = $parentScope, taskId = $taskId")

            val syncTask = syncTaskReader.getSyncTask(taskId)


            // FIXME: TODO внедрять?
            // TODO: вместо того, чтобы мудрить здесь с запуском в Scope,
            //  можно (нужно) внедрить его в класс-журналёр.

            // FIXME: проблема: если  упала сразу, до того, как
            //  запись о её начале появилась в журнале задач,
            //  сообщение об ошибке

            val taskEH = CoroutineExceptionHandler { context, throwable ->
                parentScope.launch (NonCancellable) {
//                    syncTaskNotificator.showErrorNotification(throwable, syncTask, executionId)
                    actionsOnError(throwable)
                }
            }

            parentScope.launch (Dispatchers.IO + taskEH) {
                try {
                    beforeStart()
//                    syncTaskNotificator.showProgressNotification(syncTask, executionId)

                    syncTaskProcessorFactory
                        .create(syncTask, executionId, parentScope)
                        .processSyncTask()

//                    syncTaskNotificator.showSuccessNotification(syncTask, executionId)
                    afterFinish()

                } catch (e: CancellationException) {
                    syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.CANCELLED)
                    taskLogger.logTaskCancelled(logItemId, e)
                }
            }.also { job ->
                TaskJobsHolder.addJob(taskId, job)
            }.join()

        } finally {
            finallyActions()
            TaskJobsHolder.removeJob(taskId)
        }
    }

    private suspend fun actionsOnError(throwable: Throwable) {
        taskLogger.logTaskError(logItemId, throwable)
        syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.ERROR, throwable.errorMsg)
    }

    private suspend fun beforeStart() {
        taskLogger.logTaskStarted(logItemId)
        syncTaskRunningTimeUpdater.updateStartTime(taskId)
        syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.RUNNING)
    }

    private suspend fun afterFinish() {
        syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.SUCCESS)
        taskLogger.logTaskFinished(logItemId)
    }

    private suspend fun finallyActions() {
        // TODO: ошибочное расположение
        // TODO: сделать непрерываемым
        syncTaskRunningTimeUpdater.updateFinishTime(taskId)
    }


    companion object {
        val TAG: String = SyncTaskExecutor::class.java.simpleName
    }
}


@AssistedFactory
interface SyncTaskExecutorAssistedFactory {
    fun create(taskId: String): SyncTaskExecutor
}