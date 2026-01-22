package com.github.aakumykov.sync_dir_to_cloud.loggers2.task_logger

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.toInstructionLogItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

class TaskLogger2 @AssistedInject constructor(
    @Assisted private val executionId: String,
    private val resources: Resources,
) {
    suspend fun logTaskStarted(syncTask: SyncTask) {
        Log.d(TAG, "logTaskStarted() called with: syncTask = $syncTask")
    }

    suspend fun logTaskFinished(syncTask: SyncTask) {
        Log.d(TAG, "logTaskFinished() called with: syncTask = $syncTask")
        syncTask.toInstructionLogItem(executionId, "TASK_FINISHED")
    }

    suspend fun logTaskCancelled(
        syncTask: SyncTask,
        e: CancellationException
    ) {
        Log.d(TAG, "logTaskCancelled() called with: syncTask = $syncTask, e = $e")
    }

    suspend fun logTaskError(
        syncTask: SyncTask,
        t: Throwable
    ) {
        Log.d(TAG, "logTaskError() called with: syncTask = $syncTask, t = $t")
    }

    companion object {
        val TAG: String = TaskLogger2::class.java.simpleName
    }
}


@AssistedFactory
interface TaskLogger2AssistedFactory {
    fun create(executionId: String): TaskLogger2
}