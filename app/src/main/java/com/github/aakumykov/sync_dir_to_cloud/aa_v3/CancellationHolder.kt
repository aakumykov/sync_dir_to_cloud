package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

@ExecutionScope
class CancellationHolder @Inject constructor() {

    private val jobMap: ConcurrentMap<String, Job> = ConcurrentHashMap()
    private val scopeMap: ConcurrentMap<String, CoroutineScope> = ConcurrentHashMap()

    fun addJob(operationId: String, job: Job) { jobMap[operationId] = job }
    fun getJob(operationId: String): Job? = jobMap[operationId]
    fun removeJob(operationId: String) = jobMap.remove(operationId)

    fun addScope(workerId: String, coroutineScope: CoroutineScope) { scopeMap[workerId] = coroutineScope }
    fun getScope(workerId: String): CoroutineScope? = scopeMap[workerId]
    fun removeScope(workerId: String) = scopeMap.remove(workerId)
}