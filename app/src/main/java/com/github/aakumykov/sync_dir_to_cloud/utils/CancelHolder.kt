package com.github.aakumykov.sync_dir_to_cloud.utils

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ExecutionScope
class CancelHolder @Inject constructor() {

    private val map: MutableMap<String, CoroutineScope> = mutableMapOf()

    fun putCancelHandler(operationId: String, operationScope: CoroutineScope) {
        map[operationId] = operationScope
    }

    fun getCancelHandler(operationId: String): CoroutineScope? {
        return map[operationId]
    }
}