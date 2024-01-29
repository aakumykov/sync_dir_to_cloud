package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils

class SyncTaskWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    // TODO: OutputData: краткая сводка о выполненной работе
    private val syncTaskExecutor by lazy { App.getAppComponent().getSyncTaskExecutor() }

    override suspend fun doWork(): Result {
        MyLogger.d(TAG, "doWork() [${hashCode()}] начался.")

        val taskId: String = inputData.getString(TASK_ID) ?: return failure("Входящие данные не содержат ключа '$TASK_ID'")
        val commandString: String = inputData.getString(COMMAND) ?: return failure("Входящие данные не содержат ключа '$COMMAND'")

        val command = Command.valueOf(commandString)
        MyLogger.d(TAG, "команда: $command")

        when (command) {
            Command.START -> { syncTaskExecutor.startExecutingTask(taskId) }
            Command.STOP -> { syncTaskExecutor.stopExecutingTask(taskId) }
        }

        try {
            syncTaskExecutor.startExecutingTask(taskId)
        }
        catch (t: Throwable) {
            MyLogger.e(TAG, ExceptionUtils.getErrorMessage(t), t)
            Result.failure(errorData(ExceptionUtils.getErrorMessage(t)))
        }

        val summary = syncTaskExecutor.taskSummary(taskId)
        MyLogger.d(TAG, "doWork() [${hashCode()}] завершился ($summary).")
        return Result.success(successData(summary))
    }


    private fun successData(value: String): Data {
        return Data.Builder().apply { putString(SUMMARY, value) }.build()
    }

    private fun errorData(value: String): Data {
        return Data.Builder().apply { putString(ERROR_MSG, value) }.build()
    }

    private fun failure(errorMessage: String): Result = Result.failure(errorData(errorMessage))


    companion object {

        fun startCommandData(taskId: String): Data {
            return Data.Builder().apply {
                putString(TASK_ID, taskId)
                putString(COMMAND, Command.START.name)
            }.build()
        }

        fun stopCommandData(taskId: String): Data {
            return Data.Builder().apply {
                putString(TASK_ID, taskId)
                putString(COMMAND, Command.STOP.name)
            }.build()
        }

        @Deprecated("Используй startCommandData(taskId), stopCommandData(taskId)")
        fun dataWithTaskId(taskId: String): Data = Data.Builder().putString(TASK_ID, taskId).build()

        val TAG: String = SyncTaskWorker::class.java.simpleName

        const val COMMAND: String = "COMMAND"
        const val TASK_ID: String = "TASK_ID"
        const val ERROR_MSG: String = "ERROR_MSG"
        const val SUMMARY: String = "SUMMARY"

        enum class Command { START, STOP }
    }
}