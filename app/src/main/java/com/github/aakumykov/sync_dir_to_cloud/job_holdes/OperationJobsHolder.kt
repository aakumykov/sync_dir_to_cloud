package com.github.aakumykov.sync_dir_to_cloud.job_holdes

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

object OperationJobsHolder {

    val TAG = OperationJobsHolder.javaClass.simpleName

    init { Log.d(TAG, "init{}") }

    private val map: ConcurrentMap<String, Job> = ConcurrentHashMap()

    fun addJob(jobId: String, job: Job) {
        Log.d(TAG, "addJob() called with: jobId = $jobId, job = $job")
        map[jobId] = job
    }

    fun getJob(jobId: String): Job? {
        Log.d(TAG, "getJob() called with: jobId = $jobId")
        return map[jobId]
    }

    fun removeJob(jobId: String) {
        Log.d(TAG, "removeJob() called with: jobId = $jobId")
        map.remove(jobId)
    }
}