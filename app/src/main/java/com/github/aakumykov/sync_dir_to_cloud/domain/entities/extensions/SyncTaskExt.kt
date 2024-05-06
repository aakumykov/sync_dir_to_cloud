package com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

fun SyncTask.isExecutionIntervalCahnged(): Boolean {
    return (oldIntervalH != intervalHours) || (oldIntervalM != intervalMinutes)
}

fun SyncTask.executionIntervalNotZero(): Boolean {
    return 0 != intervalHours || 0 != intervalMinutes
}

