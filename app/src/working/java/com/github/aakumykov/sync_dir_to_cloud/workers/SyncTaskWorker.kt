package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.TaskCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException

class SyncTaskWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {
    // TODO: OutputData: краткая сводка о выполненной работе

    private val coroutineDispatcher = Dispatchers.IO

    private val taskCancellationHolder: TaskCancellationHolder by lazy { appComponent.getTaskCancellationHolder() }

    private val syncTaskReader: SyncTaskReader by lazy { appComponent.getSyncTaskReader() }
    private val syncTaskStateChanger by lazy { appComponent.getSyncTaskStateChanger() }
    private val syncTaskRunningTimeUpdater by lazy { appComponent.getSyncTaskRunningTimeUpdater() }
    private var taskSummary: String? = null
    private val hashCode: String = hashCode().toString()

    // FIXME: как быть с null? По идее, нужно регистрировать это как ошибку и завершать
    // задачу как "успешную", чтобы бессмысленно не пытаться выполнить её много раз.
    // Т.е. нужен доп статус спец. для этой ситуации...
    private val taskId: String get() = inputData.getString(TASK_ID)!!

    override suspend fun doWork(): Result {
        return withContext(coroutineDispatcher) {
            try {
                appComponent.getSyncTaskExecutorAssistedFactory().create(this).also { syncTaskExecutor ->
                    taskCancellationHolder.addScope(taskId, this)
                    Log.d(TAG, "[$hashCode] Задача '$taskId' начала выполнение")
                    syncTaskExecutor.executeSyncTask(taskId)
                    Log.d(TAG, "[$hashCode] Задача '$taskId' завершила выполнение")
                }
                Result.success()
            }
            catch (e: CancellationException) {
                Log.d(TAG, "[$hashCode] Задача '$taskId' прервана пользователем")
                return@withContext Result.success()
            }
            catch (e: Exception) {
                Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
                return@withContext Result.failure()
            }
        }
    }

    /*override fun doWork(): Result {
        MyLogger.d(TAG, "[${classNameWithHash()}] doWork() начался")

        taskId = inputData.getString(TASK_ID)
            ?: return Result.failure(errorData("TASK_ID не найден во входящих данных."))

        MyLogger.d(TAG, "taskId: $taskId")

        try {
            runBlocking {
                scope = this

                // FIXME: избавиться от "!!"
                appComponent.getCancellationHolder().addScope(taskId!!, scope!!)

                syncTaskRunningTimeUpdater.updateStartTime(taskId!!)
                syncTaskRunningTimeUpdater.clearFinishTime(taskId!!)

//                MyLogger.d(TAG, "Перед 'syncTaskExecutor.executeSyncTask()'")
                syncTaskExecutor.executeSyncTask(taskId!!)
//                MyLogger.d(TAG, "После 'syncTaskExecutor.executeSyncTask()'")

                fetchTaskSummary(taskId!!)
            }
        }
        catch (t: Throwable) {
            runBlocking {
                ExceptionUtils.getErrorMessage(t).let { errorMsg ->
                    syncTaskStateChanger.changeExecutionState(taskId!!, ExecutionState.ERROR, errorMsg)
                    MyLogger.e(TAG, errorMsg, t)
                    Result.failure(errorData(errorMsg))
                }
            }
        }
        finally {
            runBlocking {
                syncTaskRunningTimeUpdater.updateFinishTime(taskId!!)
            }
        }

        MyLogger.d(TAG, "[${classNameWithHash()}] doWork() завершился.") //  ($taskSummary)
        return Result.success(successData(taskSummary!!))
    }*/

    /*override fun onStopped() {
        super.onStopped()
        runBlocking {
            syncTaskExecutor.stopExecutingTask(taskId!!)
        }
//        scope?.cancel(CancellationException("ОСТАНОВЛЕНО ВРУЧНУЮ [${hashCode}], executorHashCode: ${syncTaskExecutor.hashCode()}"))
        scope?.cancel(CancellationException("ОСТАНОВЛЕНО ВРУЧНУЮ"))
        val taskId: String? = inputData.getString(TASK_ID)
        MyLogger.d(TAG, "onStopped() [${hashCode}], taskId: $taskId")
    }*/


    private suspend fun fetchTaskSummary(taskId: String) {
//        MyLogger.d(TAG, "fetchTaskSummary(taskId: $taskId)")
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