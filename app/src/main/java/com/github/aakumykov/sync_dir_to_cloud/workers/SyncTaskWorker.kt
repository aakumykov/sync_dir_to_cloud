package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.cancellation_holders.TaskCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.config.ProgressNotificationsConfig
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.utils.SampleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

//
// FIXME: пишут, что на работу этому "воркеру" даётся 10 минут:
//  https://developer.android.com/reference/kotlin/androidx/work/CoroutineWorker
//
class SyncTaskWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {
    // TODO: OutputData: краткая сводка о выполненной работе

    init { Log.d(TAG, "init{}") }

    private val workerContext = context

    private val coroutineDispatcher = Dispatchers.IO

    private val taskCancellationHolder: TaskCancellationHolder by lazy { appComponent.getTaskCancellationHolder() }

    private val syncTaskReader: SyncTaskReader by lazy { appComponent.getSyncTaskReader() }
    private val syncTaskStateChanger by lazy { appComponent.getSyncTaskStateChanger() }
    private val syncTaskRunningTimeUpdater by lazy { appComponent.getSyncTaskRunningTimeUpdater() }
    private var taskSummary: String? = null
    private val thisObjectHashCode: String = hashCode().toString()

    // FIXME: как быть с null? По идее, нужно регистрировать это как ошибку и завершать
    // задачу как "успешную", чтобы бессмысленно не пытаться выполнить её много раз.
    // Т.е. нужен доп статус спец. для этой ситуации...
    private val taskId: String get() = inputData.getString(KEY_TASK_ID)!!

    override suspend fun doWork(): Result {
        Log.d(TAG, "[worker: $thisObjectHashCode]: doWork()")

        return CoroutineScope(coroutineDispatcher).async (coroutineDispatcher) {
            doWorkReal(this)
        }.also {
            taskJobsHolder.addJob(taskId, it)
        }.await()
    }

    private suspend fun doWorkReal(coroutineScope: CoroutineScope): androidx.work.ListenableWorker.Result {
        return try {
            SampleService.start(applicationContext)

            appComponent.getSyncTaskExecutorAssistedFactory().create(coroutineScope).also { syncTaskExecutor ->
//                taskCancellationHolder.addScope(taskId, this)

                Log.d(TAG, "[worker: $thisObjectHashCode] Задача '$taskId' начала выполнение, taskJobsHolder: ${taskJobsHolder.hashCode()}, operationJobsHolder: ${operationJobsHolder.hashCode()}")

                syncTaskExecutor.executeSyncTask(taskId)

                Log.d(TAG, "[worker: $thisObjectHashCode]: Задача '$taskId' завершила выполнение, taskJobsHolder: ${taskJobsHolder.hashCode()}, operationJobsHolder: ${operationJobsHolder.hashCode()}")
            }
            Result.success()
        }
        catch (e: CancellationException) {
            Log.w(TAG, "[worker: $thisObjectHashCode]: Задача '$taskId' прервана пользователем (${e.errorMsgExtended}) [worker:$thisObjectHashCode]")
            return Result.success()
        }
        catch (e: Exception) {
            // FIXME: не возвращать неудачный результат, а просто сихранять ошибку в SyncTask
            Log.e(TAG, "[worker: $thisObjectHashCode] ${e.errorMsgExtended} [worker:$thisObjectHashCode]")
            return Result.failure()
        }
        finally {
//            taskCancellationHolder.removeScope(taskId)
            taskJobsHolder.removeJob(taskId)
            SampleService.stop(applicationContext)
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
                e.errorMsg.let { errorMsg ->
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
//        scope?.cancel(StreamToFileCopyingCancellationException("ОСТАНОВЛЕНО ВРУЧНУЮ [${hashCode}], executorHashCode: ${syncTaskExecutor.hashCode()}"))
        scope?.cancel(StreamToFileCopyingCancellationException("ОСТАНОВЛЕНО ВРУЧНУЮ"))
        val taskId: String? = inputData.getString(TASK_ID)
        MyLogger.d(TAG, "onStopped() [${hashCode}], taskId: $taskId")
    }*/


    private suspend fun fetchTaskSummary(taskId: String) {
//        MyLogger.d(TAG, "fetchTaskSummary(taskId: $taskId)")
        taskSummary = syncTaskReader.getSyncTask(taskId).summary()
    }

    private fun successData(value: String): Data {
        return Data.Builder().apply { putString(KEY_SUMMARY, value) }.build()
    }

    private fun errorData(value: String): Data {
        return Data.Builder().apply { putString(KEY_ERROR_MSG, value) }.build()
    }

    companion object {
        val TAG: String = SyncTaskWorker::class.java.simpleName

        const val KEY_TASK_ID: String = "TASK_ID"
        const val KEY_ERROR_MSG: String = "ERROR_MSG"
        const val KEY_SUMMARY: String = "SUMMARY"

        fun dataWithTaskId(taskId: String): Data = Data.Builder().putString(KEY_TASK_ID, taskId).build()

        val taskJobsHolder = TaskJobsHolder
        val operationJobsHolder = OperationJobsHolder
    }
}

// TODO: всё-таки, хранить Job или Scope?
object TaskJobsHolder {

    val TAG = TaskJobsHolder.javaClass.simpleName

    init { Log.d(TAG, "init{}") }

    private val jobsMap: ConcurrentMap<String, Job> = ConcurrentHashMap()

    fun addJob(taskId: String, coroutineScope: Job) {
        Log.d(TAG, "addJob() called with: taskId = $taskId, coroutineScope = $coroutineScope")
        jobsMap[taskId] = coroutineScope
    }

    fun getJob(taskId: String): Job? {
        Log.d(TAG, "getJob() called with: taskId = $taskId")
        return jobsMap[taskId]
    }

    fun removeJob(taskId: String) {
        Log.d(TAG, "removeJob() called with: taskId = $taskId")
        jobsMap.remove(taskId)
    }
}

object OperationJobsHolder {

    val TAG = OperationJobsHolder.javaClass.simpleName

    init { Log.d(TAG, "init{}") }

    private val map: ConcurrentMap<String, Job> = ConcurrentHashMap()

    fun addJob(taskId: String, job: Job) {
        map[taskId] = job
    }

    fun getJob(taskId: String): Job? {
        return map[taskId]
    }

    fun removeJob(taskId: String) {
        map.remove(taskId)
    }
}

val taskJobsHolder: TaskJobsHolder get() = SyncTaskWorker.taskJobsHolder
val operationJobsHolder: OperationJobsHolder get() = SyncTaskWorker.operationJobsHolder