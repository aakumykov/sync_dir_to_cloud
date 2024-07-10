package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

/**
 * Выполняет копирование всех файловых SyncObject-ов указанного SyncTask,
 * меняя статус обрабатываемого SyncObject.
 */
class SyncTaskFilesCopier @Inject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectCopierCreator: SyncObjectCopierCreator
){
    suspend fun copyNewFilesForSyncTask(syncTask: SyncTask) {
        syncObjectReader
            .getObjectsForTaskWithModificationState(syncTask.id, ModificationState.NEW)
            .filter { !it.isDir }// TODO: заменить на isFile
            .also {
                copySyncObjects(
                    syncObjectList = it,
                    syncTask = syncTask,
                    overwriteIfExists = true
                )
        }
    }

    suspend fun copyModifiedFilesForSyncTask(syncTask: SyncTask) {
        syncObjectReader
            .getObjectsForTaskWithModificationState(syncTask.id, ModificationState.MODIFIED)
            .filter { !it.isDir } // TODO: заменить на isFile
            .also {
                copySyncObjects(
                    syncObjectList = it,
                    syncTask = syncTask,
                    overwriteIfExists = true
                )
            }
    }

    suspend fun copyNeverCopiedFilesOfSyncTask(syncTask: SyncTask) {
        syncObjectReader
            .getAllObjectsForTask(syncTask.id)
            .filter { !it.isDir } // TODO: заменить на isFile
            .filter { it.syncState == ExecutionState.NEVER }
            .also {
                copySyncObjects(
                    syncObjectList = it,
                    syncTask = syncTask,
                    overwriteIfExists = true
                )
            }
    }

    suspend fun copyInTargetLostFiles(syncTask: SyncTask) {
        syncObjectReader
            .getAllObjectsForTask(syncTask.id)
            .filter { it.isFile }
            .filter { it.notExistsInTarget }
            .also { list -> copySyncObjects(list, syncTask, false) }
    }

    private suspend fun copySyncObjects(
        syncObjectList: List<SyncObject>,
        syncTask: SyncTask,
        overwriteIfExists: Boolean
    ) {
//        Log.d(TAG, "copySyncObjects()")

        val syncObjectCopier = syncObjectCopierCreator.createFileCopierFor(syncTask)

        syncObjectList.forEach { syncObject ->

            val objectId = syncObject.id

            syncObjectStateChanger.setRestorationState(objectId, ExecutionState.RUNNING)

            syncObjectCopier
                ?.copySyncObject(syncObject, syncTask, overwriteIfExists)
                ?.onSuccess {
                    syncObjectStateChanger.setRestorationState(objectId, ExecutionState.SUCCESS)
                }
                ?.onFailure { throwable ->
                    ExceptionUtils.getErrorMessage(throwable).also { errorMsg ->
                        syncObjectStateChanger.setRestorationState(objectId, ExecutionState.ERROR, errorMsg)
                        Log.e(TAG, errorMsg, throwable)
                    }
                }
        }
    }

    companion object {
        val TAG: String = SyncTaskFilesCopier::class.java.simpleName
    }
}

val SyncObject.isFile get() = !isDir

val SyncObject.notExistsInTarget get() = !isExistsInTarget