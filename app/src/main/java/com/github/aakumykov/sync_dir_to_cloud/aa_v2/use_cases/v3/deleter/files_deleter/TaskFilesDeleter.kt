package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.files_deleter

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files.isFile
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TaskFilesDeleter @AssistedInject constructor(
    @Assisted private val fileDeleter: FileDeleter,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
) {
    suspend fun deleteDeletedFilesForTask(syncTask: SyncTask) {
        syncObjectReader.getObjectsForTaskWithModificationState(syncTask.id, ModificationState.DELETED)
            .filter { it.isFile }
            .also { list -> processList(list, syncTask) }
    }

    // TODO: специальный статус "удаляется"
    private suspend fun processList(list: List<SyncObject>, syncTask: SyncTask) {
        list.forEach { syncObject ->

            syncObjectStateChanger.markAsBusy(syncObject.id)

            fileDeleter.deleteFile(syncObject)
                .onSuccess {
                    syncObjectStateChanger.markAsSuccessfullySynced(syncObject.id)
                }
                .onFailure {
                    ExceptionUtils.getErrorMessage(it).also { errorMsg ->
                        syncObjectStateChanger.markAsError(syncObject.id, errorMsg)
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