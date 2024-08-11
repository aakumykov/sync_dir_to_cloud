package com.github.aakumykov.sync_dir_to_cloud.sync_object_logger

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectLogRepository
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.executionId
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

@ExecutionScope
class SyncObjectLogger @Inject constructor(
    private val syncObjectLogRepository: SyncObjectLogRepository
) {
    private var _executionId: String? = null
    private val executionId: String get() = _executionId!!

    fun init(executionId: String): SyncObjectLogger {
        _executionId = executionId
        return this
    }

    suspend fun log(syncObjectLogItem: SyncObjectLogItem) {
        syncObjectLogRepository.addLogItem(executionId, syncObjectLogItem)
    }
}