package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.exceptions

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

class SyncObjectError(
    val syncObject: SyncObject,
    val operationName: Int,
    errorMsg: String
): Exception(errorMsg)