package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import dagger.assisted.AssistedFactory
import kotlinx.coroutines.CoroutineScope

@AssistedFactory
interface SyncTaskExecutorAssistedFactory {
    fun create(taskExecutingScope: CoroutineScope): SyncTaskExecutor
}