package com.github.aakumykov.sync_dir_to_cloud.job_holdes

import android.util.Log
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

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