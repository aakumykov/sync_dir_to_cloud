package com.github.aakumykov.sync_dir_to_cloud.cancellation_holders

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

@ExecutionScope
class OperationCancellationHolder @Inject constructor() {

    private val jobMap: ConcurrentMap<String, Job> = ConcurrentHashMap()

    fun addJob(id: String, job: Job) { jobMap[id] = job }
    fun getJob(id: String): Job? = jobMap[id]
    fun removeJob(id: String) = jobMap.remove(id)
}