package com.github.aakumykov.sync_dir_to_cloud.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.job_holders.operationJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.job_holders.taskJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.utils.SampleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext


// FIXME: пишут, что на работу этому "воркеру" даётся 10 минут:
//  https://developer.android.com/reference/kotlin/androidx/work/CoroutineWorker

/**
 * Задача Worker-а - запустить задачу на выполнение, сопроводив это отображением уведомления
 * (которое, уведомление) используется для того, чтобы worker не умирал при закрытии экрана.
 */
class SyncTaskWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    private val thisObjectHashCode: String = hashCode().toString()


    override suspend fun doWork(): Result {
        Log.d(TAG, "[worker: $thisObjectHashCode]: doWork()")

        val taskId: String? = inputData.getString(KEY_TASK_ID)

        if (null == taskId) {
            // TODO: показывать уведомление об ошибке
            Log.e(TAG, "!!! There is no TASK_ID argument passed to $TAG. Cannot work. !!!")
            return Result.success()
        }

        val job: Job = appComponent.getTaskJob()
        val supJob = SupervisorJob(job)
        val dispatcher = appComponent.getTaskDispatcher()

        withContext ( supJob + dispatcher) {
            try {
                taskJobsHolder.addJob(taskId, supJob)
                doWorkReal(this, taskId)
            } catch (t: Throwable) {
                Log.e(TAG, t.errorMsgExtended)
            }
        }

        return Result.success()
    }


    private suspend fun doWorkReal(coroutineScope: CoroutineScope, taskId: String) {
        try {
            SampleService.start(applicationContext)

            appComponent.getSyncTaskExecutorAssistedFactory().create(coroutineScope).also { syncTaskExecutor ->
                Log.d(TAG, "[worker: $thisObjectHashCode] Задача '$taskId' начала выполнение, taskJobsHolder: ${taskJobsHolder.hashCode()}, operationJobsHolder: ${operationJobsHolder.hashCode()}")

                syncTaskExecutor.executeSyncTask(taskId)

                Log.d(TAG, "[worker: $thisObjectHashCode]: Задача '$taskId' завершила выполнение, taskJobsHolder: ${taskJobsHolder.hashCode()}, operationJobsHolder: ${operationJobsHolder.hashCode()}")
            }
        }
        catch (t: Throwable) {
            Log.w(TAG, "[worker: $thisObjectHashCode] ${t.errorMsgExtended} [worker:$thisObjectHashCode]")
//            t.printStackTrace()
        }
        finally {
            taskJobsHolder.removeJob(taskId)

            SampleService.stop(applicationContext)
        }
    }


    companion object {
        val TAG: String = SyncTaskWorker::class.java.simpleName

        const val KEY_TASK_ID: String = "TASK_ID"

        fun dataWithTaskId(taskId: String): Data = Data.Builder().putString(KEY_TASK_ID, taskId).build()
    }
}