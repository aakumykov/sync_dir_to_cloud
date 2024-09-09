package com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

@AppScope
class TaskCancellationHolder @Inject constructor() {

    private val scopeMap: ConcurrentMap<String, CoroutineScope> = ConcurrentHashMap()

    fun addScope(taskId: String, coroutineScope: CoroutineScope) { scopeMap[taskId] = coroutineScope }
    fun getScope(taskId: String): CoroutineScope? = scopeMap[taskId]
    fun removeScope(taskId: String) = scopeMap.remove(taskId)
}