package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem

interface SyncObjectLogReader {
    suspend fun getList(taskId: String, executionId: String): List<SyncObjectLogItem>
    fun getListAsLiveData(taskId: String, executionId: String): LiveData<List<SyncObjectLogItem>>
}