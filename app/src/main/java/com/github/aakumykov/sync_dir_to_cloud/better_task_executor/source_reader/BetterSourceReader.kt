package com.github.aakumykov.sync_dir_to_cloud.better_task_executor.source_reader

import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.exceptions.TaskExecutionException
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class BetterSourceReader @AssistedInject constructor(
    @Assisted val syncTask: SyncTask,
    private val syncObjectAdder: SyncObjectAdder,
    private val syncTaskStateChanger: SyncTaskStateChanger,
) {
    @Throws(TaskExecutionException.SourceReadingException::class)
    suspend fun readSource() {

    }
}