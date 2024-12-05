package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

class WrappedSyncObject(
    val syncObject: SyncObject,
    val operationId: String,
) {
    companion object {
        fun wrapList(
            list: List<SyncObject>,
            operationId: String
        ): List<WrappedSyncObject> = list.map { WrappedSyncObject(it,operationId) }
    }
}