package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.extensions.classNameWithHash
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.extensions.tag
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskRunningTimeUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.job_holdes.TaskJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.loggers2.task_logger.TaskLogger
import com.github.aakumykov.sync_dir_to_cloud.loggers2.task_logger.TaskLoggerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificatorAssistedFactory
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
    private val syncTaskNotificatorAssistedFactory: SyncTaskNotificatorAssistedFactory
){
    private val syncTaskRunningTimeUpdater: SyncTaskRunningTimeUpdater by lazy {
        appComponent.getSyncTaskRunningTimeUpdater() }

    private val executionId: String by lazy { hashCode().toString() }

    private val logItemId: String by lazy { newRandomId }

    private val taskLogger: TaskLogger by lazy {
        taskLoggerAssistedFactory.create(taskId, executionId)
    }

    private fun getNotificator(syncTask: SyncTask): SyncTaskNotificator = syncTaskNotificatorAssistedFactory.create(syncTask)


    suspend fun executeSyncTask(parentScope: CoroutineScope, taskId: String) {
        try {
            Log.d(TAG, "executeSyncTask() called with: scope = $parentScope, taskId = $taskId")

            val syncTask = syncTaskReader.getSyncTask(taskId)

            val notificator = getNotificator(syncTask)


            // FIXME: TODO внедрять?
            // TODO: вместо того, чтобы мудрить здесь с запуском в Scope,
            //  можно (нужно) внедрить его в класс-журналёр.
            val taskEH = CoroutineExceptionHandler { context, throwable ->
                parentScope.launch (NonCancellable) {
                    taskLogger.logTaskError(logItemId, throwable)
                    syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.ERROR, throwable.errorMsg)
                    notificator.showErrorNotification(throwable)
                }
            }

            parentScope.launch (Dispatchers.IO + taskEH) {
                try {
                    notificator.showProgressNotification()
                    executeSyncTaskReal(this, syncTask)
                    notificator.showSuccessNotification()
                } catch (e: CancellationException) {
                    syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.CANCELLED)
                    taskLogger.logTaskCancelled(logItemId, e)
                } finally {
                    notificator.hideProgressNotification()
                }
            }.also { job ->
                TaskJobsHolder.addJob(taskId, job)
            }.join() // Этот join() нужен для синхронного выполнения метода в scope.

        } finally {
            TaskJobsHolder.removeJob(taskId)
        }
    }


    private suspend fun executeSyncTaskReal(
        parentScope: CoroutineScope,
        syncTask: SyncTask,
    ) {
        Log.d(tag, "========= executeSyncTaskReal() [${classNameWithHash()}] СТАРТ ========")

        val taskId = syncTask.id

        try {
            taskLogger.logTaskStarted(logItemId)
            syncTaskRunningTimeUpdater.updateStartTime(taskId)
            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.RUNNING)

            syncTaskProcessorFactory
                .create(syncTask, executionId, parentScope)
                .processSyncTask()

            syncTaskStateChanger.changeExecutionState(taskId, ExecutionState.SUCCESS)
            taskLogger.logTaskFinished(logItemId)
        }
        finally {
            // TODO: ошибочное расположение
            // TODO: сделать не прерываемым
            syncTaskRunningTimeUpdater.updateFinishTime(taskId)
        }

        Log.d(tag, "========= executeSyncTaskReal() [${classNameWithHash()}] ФИНИШ ========")
    }

    companion object {
        val TAG: String = SyncTaskExecutor::class.java.simpleName
    }
}


@AssistedFactory
interface SyncTaskExecutorAssistedFactory {
    fun create(taskId: String): SyncTaskExecutor
}