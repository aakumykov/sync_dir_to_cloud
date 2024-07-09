package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.dirs_deleter

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import javax.inject.Inject

class TaskDirsDeleterCreator @Inject constructor(
    private val taskDirsDeleterAssistedFactory: TaskDirsDeleterAssistedFactory,
    private val dirDeleterCreator: DirDeleterCreator
){
    suspend fun createTaskDirsDeleterForTask(syncTask: SyncTask): TaskDirsDeleter? {
        return dirDeleterCreator.create(syncTask)?.let { dirDeleter ->
            taskDirsDeleterAssistedFactory.create(dirDeleter)
        }
    }
}
