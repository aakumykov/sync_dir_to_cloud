package com.github.aakumykov.sync_dir_to_cloud.view.task_details

import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

data class TaskDetailsItem(
    val taskId: String,
    val executionId: String,
    val startTimestamp: Long,
    val finishTimestamp: Long,
    val logItemType: LogItemType,
) {
    companion object {
        fun fromSyncLog(list: List<LogOfSync>): TaskDetailsItem {
            return TaskDetailsItem(
                taskId = list.first().taskId,
                executionId = list.first().executionId,
                startTimestamp = list.minByOrNull { it.startTime }?.startTime ?: 0L,
                finishTimestamp = list.maxByOrNull { it.finishTime }?.finishTime ?: 0L,
                logItemType = LogItemType.Companion.toSummarizedType(list.map { it.logItemType })
            )
        }

    }
}