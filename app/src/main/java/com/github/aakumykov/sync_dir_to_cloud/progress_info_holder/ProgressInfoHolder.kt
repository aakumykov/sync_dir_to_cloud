package com.github.aakumykov.sync_dir_to_cloud.progress_info_holder

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import java.util.Collections
import javax.inject.Inject

@AppScope
// TODO: @WorkerScope, @TaskDetailsScope
class ProgressInfoHolder @Inject constructor() {

    private val map = Collections.synchronizedMap(HashMap<String,ProgressInfo>())


    fun addProgressInfo(progressInfo: ProgressInfo) {
        map[progressInfo.syncObjectId] = progressInfo
    }

    fun removeProgressInfo(objectId: String) {
        map.remove(objectId)
    }


    fun setProgress(objectId: String, transferredBytes: Long) {
//        Log.d(TAG, "setProgress() called with: objectId = $objectId, transferredBytes = $transferredBytes")
        map[objectId]?.transferredBytes = transferredBytes
    }

    fun getProgress(objectId: String): Long? {
        return map[objectId]?.transferredBytes
    }


    companion object {
        val TAG: String = ProgressInfoHolder::class.java.simpleName
    }



    data class ProgressInfo (
        val isDir: Boolean,
        val syncObjectId: String,
        var sizeBytes: Long,
        var transferredBytes: Long
    )
}