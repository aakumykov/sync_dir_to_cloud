package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.utils.SampleService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


// FIXME: пишут, что на работу этому "воркеру" даётся 10 минут:
//  https://developer.android.com/reference/kotlin/androidx/work/CoroutineWorker

/**
 * Задача Worker-а - запустить задачу на выполнение, сопроводив это отображением уведомления
 * (которое, уведомление) используется для того, чтобы worker не умирал при закрытии экрана.
 */
class SyncTaskWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    // TODO: внедрять диспетчер
    private val coroutineDispatcher = Dispatchers.IO
    // TODO: а scope?

    private val thisObjectHashCode: String = hashCode().toString()

    init {
        Log.d(TAG, "init{} [hashCode:${hashCode()}]")
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork() [worker: $thisObjectHashCode]")

        val taskId = inputData.getString(KEY_TASK_ID)

        if (null == taskId) {
            // TODO: показывать уведомление об ошибке
            Log.e(TAG, "!!! There is no TASK_ID argument passed to $TAG. Cannot work. !!!")
            return Result.success()
        }

        val scope = CoroutineScope(coroutineDispatcher)

        Log.d(TAG, "перед doWorkReal()")
        doWorkReal(taskId = taskId, scope)
        Log.d(TAG, "после doWorkReal()")

        return Result.success()
    }


    private suspend fun doWorkReal(taskId: String, scope: CoroutineScope) {
        Log.d(TAG, "doWorkReal() called with: taskId = $taskId, scope = $scope")
        try {
            SampleService.start(applicationContext)

            appComponent.getSyncTaskExecutor().also { syncTaskExecutor ->
                Log.d(TAG, "doWorkReal() [worker: $thisObjectHashCode]: Задача taskId: $taskId начала    выполнение, taskJobsHolder: ${taskJobsHolder.hashCode()}, operationJobsHolder: ${operationJobsHolder.hashCode()}")
                syncTaskExecutor.executeSyncTask(scope, taskId = taskId)
                Log.d(TAG, "doWorkReal() [worker: $thisObjectHashCode]: Задача taskId: $taskId завершила выполнение, taskJobsHolder: ${taskJobsHolder.hashCode()}, operationJobsHolder: ${operationJobsHolder.hashCode()}")
            }
        }
        catch (e: CancellationException) {
            // TODO: если система сама отменяет Worker, нужно в этом месте останавливать задачу.
            Log.w(TAG, "doWorkReal() Корутина отменена: ${e.errorMsgExtended}")
        }
        catch (t: Throwable) {
            Log.e(TAG, "doWorkReal() [worker: $thisObjectHashCode] ${t.errorMsgExtended} [worker:$thisObjectHashCode]")
            Log.e(TAG, t.errorMsgExtended)
        }
        finally {
            Log.d(TAG, "doWorkReal() finally{}")
            taskJobsHolder.removeJob(taskId)
            SampleService.stop(applicationContext)
        }
    }


    companion object {
        val TAG: String = SyncTaskWorker::class.java.simpleName

        const val KEY_TASK_ID: String = "TASK_ID"

        fun dataWithTaskId(taskId: String): Data = Data.Builder().putString(KEY_TASK_ID, taskId).build()

        @Deprecated("разобраться, где это держать")
        val taskJobsHolder = TaskJobsHolder

        @Deprecated("разобраться, где это держать")
        val operationJobsHolder = OperationJobsHolder
    }
}


// TODO: всё-таки, хранить Job или Scope?
object TaskJobsHolder {

    val TAG: String = TaskJobsHolder.javaClass.simpleName

    init { Log.d(TAG, "init{}") }

    private val jobsMap: ConcurrentMap<String, Job> = ConcurrentHashMap()

    fun addJob(taskId: String, job: Job) {
        Log.d(TAG, "[${hashCode()}] addJob(): taskId:$taskId, $job")
        jobsMap[taskId] = job
    }

    fun getJob(taskId: String): Job? {
        return jobsMap[taskId].also {
            Log.d(TAG, "[${hashCode()}] getJob(): taskId:$taskId, $it")
        }
    }

    fun removeJob(taskId: String) {
        jobsMap.remove(taskId).also {
            Log.d(TAG, "[${hashCode()}] removeJob(): taskId:$taskId, $it")
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

@Deprecated("разобраться, где это держать")
val taskJobsHolder: TaskJobsHolder get() = SyncTaskWorker.taskJobsHolder

@Deprecated("разобраться, где это держать")
val operationJobsHolder: OperationJobsHolder get() = SyncTaskWorker.operationJobsHolder