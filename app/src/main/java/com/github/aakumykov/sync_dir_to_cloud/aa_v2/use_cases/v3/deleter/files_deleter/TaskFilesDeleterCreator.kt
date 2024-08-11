package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.files_deleter

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import javax.inject.Inject

class TaskFilesDeleterCreator @Inject constructor(
    private val assistedFactory: TaskFilesDeleterAssistedFactory,
    private val fileDeleterCreator: FileDeleterCreator,
) {
    suspend fun createDeletedFilesDeleterForTask(syncTask: SyncTask, executionId: String): TaskFilesDeleter? {
        return fileDeleterCreator.createFileDeleterForTask(syncTask)?.let { fileDeleter ->
            assistedFactory.create(fileDeleter, executionId)
        }
    }
}