package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.dirs_deleter

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.names
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeleted
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isTargetReadingOk
import com.github.aakumykov.sync_dir_to_cloud.helpers.ExecutionLoggerHelper
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

// TODO: помечать объект как "удаляется"
class TaskDirsDeleter @AssistedInject constructor(
    @Assisted private val executionId: String,
    @Assisted private val dirDeleter: DirDeleter,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectDeleter: SyncObjectDeleter,
    private val executionLoggerHelper: ExecutionLoggerHelper,
){
    suspend fun deleteDeletedDirsForTask(taskId: String) {
        try {
            syncObjectReader.getAllObjectsForTask(taskId)
                .filter { it.isDir }
                .filter { it.isDeleted }
                .filter { it.isTargetReadingOk }
                .also { list ->
                    if (list.isNotEmpty()) {
                        executionLoggerHelper.logStart(taskId, executionId, R.string.EXECUTION_LOG_deleting_deleted_dirs)
                        processList(list)
                    }
                }
        } catch (e: Exception) {
            executionLoggerHelper.logError(taskId, executionId, TAG, e)
        }
    }

    private suspend fun processList(list: List<SyncObject>) {
        list.forEach { syncObject ->

            val objectId = syncObject.id

            syncObjectStateChanger.setDeletionState(objectId, ExecutionState.RUNNING)

            dirDeleter.deleteDir(syncObject)
                .onSuccess {
                    // менять "статус удаления" на "успешно" не нужно, так как запись удаляется из таблицы
                    syncObjectDeleter.deleteObjectWithDeletedState(objectId)
                }
                .onFailure {
                    ExceptionUtils.getErrorMessage(it).also { errorMsg ->
                        syncObjectStateChanger.markAsError(objectId, errorMsg)
                        Log.e(TAG, errorMsg, it)
                    }
                }
        }
    }

    companion object {
        val TAG: String = TaskDirsDeleter::class.java.simpleName
    }
}


@AssistedFactory
interface TaskDirsDeleterAssistedFactory {
    fun create(executionId: String, dirDeleter: DirDeleter): TaskDirsDeleter
}