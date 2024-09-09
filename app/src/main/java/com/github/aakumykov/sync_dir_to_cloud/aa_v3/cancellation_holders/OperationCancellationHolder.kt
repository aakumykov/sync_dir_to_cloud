package com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

@ExecutionScope
class OperationCancellationHolder @Inject constructor() {

    private val jobMap: ConcurrentMap<String, Job> = ConcurrentHashMap()

    fun addJob(operationId: String, job: Job) { jobMap[operationId] = job }
    fun getJob(operationId: String): Job? = jobMap[operationId]
    fun removeJob(operationId: String) = jobMap.remove(operationId)
}