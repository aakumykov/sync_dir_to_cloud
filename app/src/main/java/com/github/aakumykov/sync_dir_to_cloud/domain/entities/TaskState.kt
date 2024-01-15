package com.github.aakumykov.sync_dir_to_cloud.domain.entities

data class TaskState (
    val taskId: String,
    val state: SyncTask.State,
    val notificationId: Int
)