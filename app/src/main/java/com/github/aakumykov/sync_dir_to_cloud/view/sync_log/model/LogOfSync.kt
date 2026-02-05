package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model

import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType

data class LogOfSync(
    val timestamp: Long,
    val logItemType: LogItemType,
    val taskId: String,
    val executionId: String,
    val jobId: String? = null,
    val text: String,
    val subText: String? = null,
    val progress: Int? = null,
) {
    override fun toString(): String {
        return "LogOfSync(" +
                "timestamp=$timestamp, " +
                "logItemType=$logItemType, " +
                "text='$text', " +
                "taskId='$taskId', " +
                "executionId='$executionId', " +
                "jobId=$jobId, " +
                "subText='$subText', " +
                "progress=$progress" +
                ")"
    }

}