package com.github.aakumykov.sync_dir_to_cloud.job_holdes

import android.util.Log
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

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