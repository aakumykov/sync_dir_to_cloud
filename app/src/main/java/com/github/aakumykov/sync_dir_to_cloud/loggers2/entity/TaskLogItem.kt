package com.github.aakumykov.sync_dir_to_cloud.loggers2.entity

data class TaskLogItem(
    val id: String,
    val taskId: String,
    val executionId: String,
    val message: String,
    val timestamp: Long,
) {
}