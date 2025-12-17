package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.cancellation_holders.TaskCancellationHolder
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

    private val coroutineDispatcher = Dispatchers.IO
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


    private suspend fun doWorkReal(coroutineScope: CoroutineScope): Result {
        return try {
            SampleService.start(applicationContext)

            appComponent.getSyncTaskExecutorAssistedFactory().create(coroutineScope).also { syncTaskExecutor ->
                Log.d(TAG, "[worker: $thisObjectHashCode] Задача '$taskId' начала выполнение, taskJobsHolder: ${taskJobsHolder.hashCode()}, operationJobsHolder: ${operationJobsHolder.hashCode()}")
                syncTaskExecutor.executeSyncTask(taskId)
                Log.d(TAG, "[worker: $thisObjectHashCode]: Задача '$taskId' завершила выполнение, taskJobsHolder: ${taskJobsHolder.hashCode()}, operationJobsHolder: ${operationJobsHolder.hashCode()}")
            }
            Result.success()
        }
        catch (t: Throwable) {
            Log.e(TAG, "[worker: $thisObjectHashCode] ${e.errorMsgExtended} [worker:$thisObjectHashCode]")
            Log.e(TAG, t.errorMsgExtended)
            return Result.success()
        }
        finally {
            taskJobsHolder.removeJob(taskId)

            SampleService.stop(applicationContext)
        }
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

    fun addJob(taskId: String, job: Job) {
        Log.d(TAG, "[${hashCode()}] addJob(): taskId:$taskId, job.${job.hashCode()}")
        jobsMap[taskId] = job
    }

    fun getJob(taskId: String): Job? {
        return jobsMap[taskId].also {
            Log.d(TAG, "[${hashCode()}] getJob(): taskId:$taskId, job.${it.hashCode()}")
        }
    }

    fun removeJob(taskId: String) {
        jobsMap.remove(taskId).also {
            Log.d(TAG, "[${hashCode()}] removeJob(): taskId:$taskId, job.${it.hashCode()}")
        }
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