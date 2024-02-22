package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader

class TaskInfoViewModel(
    private var syncTaskReader: SyncTaskReader,
    private val syncObjectReader: SyncObjectReader
) : ViewModel() {

    suspend fun getSyncTask(taskId: String): LiveData<SyncTask> {
        return syncTaskReader.getSyncTaskAsLiveData(taskId)
    }

    suspend fun getSyncObjectList(taskId: String): LiveData<List<SyncObject>> {
        return syncObjectReader.getSyncObjectListAsLiveData(taskId)
    }
}
