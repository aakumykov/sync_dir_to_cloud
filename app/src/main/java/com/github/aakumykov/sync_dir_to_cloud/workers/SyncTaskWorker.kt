package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import java.util.Date
import java.util.concurrent.CancellationException

class SyncTaskWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    // TODO: OutputData: краткая сводка о выполненной работе

    private val syncTaskExecutor: SyncTaskExecutor by lazy { App.getAppComponent().getSyncTaskExecutor() }
    private val syncTaskReader: SyncTaskReader by lazy { App.getAppComponent().getSyncTaskReader() }
    private val syncTaskStateChanger by lazy { App.getAppComponent().getSyncTaskStateChanger() }
    private val syncTaskRunningTimeUpdater by lazy { App.getAppComponent().getSyncTaskRunningTimeUpdater() }
    private var taskSummary: String? = null
    private val hashCode: String = hashCode().toString()
    private var scope: CoroutineScope? = null
    private var taskId: String? = null

    override fun doWork(): Result {
        MyLogger.d(TAG, "doWork() [${hashCode}] начался, executorHashCode: ${syncTaskExecutor.hashCode()}")

        taskId = inputData.getString(TASK_ID)
            ?: return Result.failure(errorData("TASK_ID не найден во входящих данных."))

        MyLogger.d(TAG, "taskId: $taskId")

        try {
            runBlocking {
                scope = this

                syncTaskRunningTimeUpdater.updateStartTime(taskId!!)
                syncTaskRunningTimeUpdater.clearFinishTime(taskId!!)

                MyLogger.d(TAG, "Перед 'syncTaskExecutor.executeSyncTask()'")
                syncTaskExecutor.executeSyncTask(taskId!!)
                MyLogger.d(TAG, "После 'syncTaskExecutor.executeSyncTask()'")

                fetchTaskSummary(taskId!!)
            }
        }
        catch (t: Throwable) {
            runBlocking {
                val errorMsg = ExceptionUtils.getErrorMessage(t)

                syncTaskStateChanger.changeExecutionState(taskId!!, ExecutionState.ERROR, errorMsg)
                MyLogger.e(TAG, errorMsg, t)

                Result.failure(errorData(ExceptionUtils.getErrorMessage(t)))
            }
        }
        finally {
            runBlocking {
                syncTaskRunningTimeUpdater.updateFinishTime(taskId!!)
            }
        }

        MyLogger.d(TAG, "doWork() [${hashCode}] завершился ($taskSummary).")
        return Result.success(successData(taskSummary!!))
    }

    override fun onStopped() {
        super.onStopped()
        runBlocking {
            syncTaskExecutor.stopExecutingTask(taskId!!)
        }
//        scope?.cancel(CancellationException("ОСТАНОВЛЕНО ВРУЧНУЮ [${hashCode}], executorHashCode: ${syncTaskExecutor.hashCode()}"))
        scope?.cancel(CancellationException("ОСТАНОВЛЕНО ВРУЧНУЮ"))
        val taskId: String? = inputData.getString(TASK_ID)
        MyLogger.d(TAG, "onStopped() [${hashCode}], taskId: $taskId")
    }


    private suspend fun fetchTaskSummary(taskId: String) {
        MyLogger.d(TAG, "fetchTaskSummary(taskId: $taskId)")
        taskSummary = syncTaskReader.getSyncTask(taskId).summary()
    }

    private fun successData(value: String): Data {
        return Data.Builder().apply { putString(SUMMARY, value) }.build()
    }

    private fun errorData(value: String): Data {
        return Data.Builder().apply { putString(ERROR_MSG, value) }.build()
    }

    companion object {
        fun dataWithTaskId(taskId: String): Data
            = Data.Builder().putString(TASK_ID, taskId).build()

        val TAG: String = SyncTaskWorker::class.java.simpleName

        const val TASK_ID: String = "TASK_ID"
        const val ERROR_MSG: String = "ERROR_MSG"
        const val SUMMARY: String = "SUMMARY"
    }
}