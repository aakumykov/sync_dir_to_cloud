package com.github.aakumykov.sync_dir_to_cloud.progress_holder

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import javax.inject.Inject

@ExecutionScope
class ProgressHolder @Inject constructor() {

    // ObjectId --> Progress (fraction 0.xx)
    private val progressMap: MutableMap<String, Float> = mutableMapOf()

    init {
        Log.d(TAG, "init{}")
    }

    fun putProgress(objectId: String, progress: Float) {
        Log.d(TAG, "putProgress(), objectId: $objectId, progress: $progress")
        progressMap[objectId] = progress
    }

    fun getProgress(objectId: String): Float? {
        return progressMap[objectId]
    }

    fun eraseProgress(objectId: String) {
        progressMap.remove(objectId)
    }

    companion object {
        val TAG: String = ProgressHolder::class.java.simpleName
    }
}