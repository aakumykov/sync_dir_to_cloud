package com.github.aakumykov.sync_dir_to_cloud.extensions

import androidx.annotation.LongDef
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

val SyncTask.lastStartTime: Long
    get() = lastStart ?: throw IllegalStateException("SyncTask has no info about its starting time")