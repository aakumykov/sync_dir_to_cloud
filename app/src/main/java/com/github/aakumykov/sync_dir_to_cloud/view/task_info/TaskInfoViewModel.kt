package com.github.aakumykov.sync_dir_to_cloud.view.task_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader

class TaskInfoViewModel(private val syncObjectReader: SyncObjectReader) : ViewModel() {

    suspend fun getSyncObjectList(taskId: String): LiveData<List<SyncObject>> {
        return syncObjectReader.getSyncObjectListAsLiveData(taskId)
    }
}
