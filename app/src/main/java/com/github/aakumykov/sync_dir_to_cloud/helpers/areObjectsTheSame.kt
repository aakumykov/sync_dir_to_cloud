package com.github.aakumykov.sync_dir_to_cloud.helpers

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.extensions.isSameWith

fun areObjectsTheSame(o1: SyncObject, o2: SyncObject): Boolean {
    return o1.isSameWith(o2)
}