package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.files_deleter

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeleted
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isTargetReadingOk
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TaskFilesDeleter @AssistedInject constructor(
    @Assisted private val fileDeleter: FileDeleter,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectDeleter: SyncObjectDeleter,
) {
    suspend fun deleteDeletedFilesForTask(syncTask: SyncTask) {
        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isFile }
            .filter { it.isDeleted }
            .filter { it.isTargetReadingOk }
            .also { list ->
                Log.d(TAG + "_" + SyncTaskExecutor.TAG, "deleteDeletedFilesForTask(${list.size})")
                processList(list, syncTask)
            }
    }

    // TODO: специальный статус "удаляется"
    private suspend fun processList(list: List<SyncObject>, syncTask: SyncTask) {
        list.forEach { syncObject ->

            val objectId = syncObject.id

            syncObjectStateChanger.setDeletionState(objectId, ExecutionState.RUNNING)

            fileDeleter.deleteFile(syncObject)
                .onSuccess {
                    // менять "статус удаления" на "успешно" не нужно, так как запись удаляется из таблицы
                    syncObjectDeleter.deleteObjectWithDeletedState(objectId)
                }
                .onFailure {
                    ExceptionUtils.getErrorMessage(it).also { errorMsg ->
                        syncObjectStateChanger.setDeletionState(objectId, ExecutionState.ERROR, errorMsg)
                        Log.e(TAG, errorMsg, it)
                    }
                }
        }
    }

    companion object {
        val TAG: String = TaskFilesDeleter::class.java.simpleName
    }
}

@AssistedFactory
interface TaskFilesDeleterAssistedFactory {
    fun create(fileDeleter: FileDeleter): TaskFilesDeleter
}