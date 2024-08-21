package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem

interface SyncObjectLogAdder {
    suspend fun addLogItem(syncObjectLogItem: SyncObjectLogItem)
}