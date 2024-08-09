package com.github.aakumykov.sync_dir_to_cloud.sync_object_logger

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectLogRepository
import javax.inject.Inject

@ExecutionScope
class SyncObjectLogger @Inject constructor(
    private val syncObjectLogRepository: SyncObjectLogRepository
) {
    suspend fun log(syncObjectLogItem: SyncObjectLogItem) {
        syncObjectLogRepository.addLogItem(syncObjectLogItem)
    }
}