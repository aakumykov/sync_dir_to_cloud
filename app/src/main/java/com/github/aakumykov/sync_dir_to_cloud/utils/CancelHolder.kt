package com.github.aakumykov.sync_dir_to_cloud.utils

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import javax.inject.Inject

@ExecutionScope
class CancelHolder @Inject constructor() {

    private val map: MutableMap<String, Job> = mutableMapOf()

    fun putCancelHandler(operationId: String, operationScope: Job) {
        map[operationId] = operationScope
    }

    fun getCancelHandler(operationId: String): Job? {
        return map[operationId]
    }

    fun removeHandler(operationId: String) {
        map.remove(operationId)
    }
}