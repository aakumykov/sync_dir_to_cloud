package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Deprecated("убрать")
object ProgressHolder {

    private val jobToProgressMap: ConcurrentMap<String, MutableState<Float>> = ConcurrentHashMap()

    fun addProgressState(logItemId: String) {
        jobToProgressMap[logItemId] = mutableFloatStateOf(0f)
    }

    fun removeProgressState(logItemId: String) {
        jobToProgressMap.remove(logItemId)
    }

    fun getProgressState(logItemId: String): State<Float>? {
        return jobToProgressMap[logItemId]
    }

    fun setProgress(logItemId: String, progress: Float) {
        jobToProgressMap[logItemId]?.value = progress
    }
}