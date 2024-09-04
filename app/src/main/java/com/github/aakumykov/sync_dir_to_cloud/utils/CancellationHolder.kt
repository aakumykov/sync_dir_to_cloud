package com.github.aakumykov.sync_dir_to_cloud.utils

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import com.github.aakumykov.sync_dir_to_cloud.utils.counting_buffered_streams.CancelableInputStream
import javax.inject.Inject

@ExecutionScope
class CancellationHolder @Inject constructor() {

    private val map: MutableMap<String, CancelableInputStream.CancellationMarker> = mutableMapOf()

    fun putCancellationMarker(syncObjectLogItemId: String, cancellationMarker: CancelableInputStream.CancellationMarker) {

    }

    fun getCancellationMarker(syncObjectLogItemId: String): CancelableInputStream.CancellationMarker? {
        return map[syncObjectLogItemId]
    }

    companion object {
        fun idFor(taskId: String, objectId: String, executionId: String): String {
            return "${taskId}_${objectId}_${executionId}"
        }
    }
}