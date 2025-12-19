package com.github.aakumykov.sync_dir_to_cloud.job_holders

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.workers.SyncTaskWorker
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

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


@Deprecated("разобраться, где это держать")
val taskJobsHolder: TaskJobsHolder get() = TaskJobsHolder
