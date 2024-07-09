package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.dirs_deleter

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target.dirs.isDeleted
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

// TODO: помечать объект как "удаляется"
class TaskDirsDeleter @AssistedInject constructor(
    @Assisted private val dirDeleter: DirDeleter,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectDeleter: SyncObjectDeleter
){
    suspend fun deleteDeletedDirsForTask(taskId: String) {
        syncObjectReader.getObjectsForTaskWithModificationState(taskId, ModificationState.DELETED)
            .filter { it.isDir }
            .filter { it.isDeleted }
            .also { list -> processList(list) }
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
    fun create(dirDeleter: DirDeleter): TaskDirsDeleter
}