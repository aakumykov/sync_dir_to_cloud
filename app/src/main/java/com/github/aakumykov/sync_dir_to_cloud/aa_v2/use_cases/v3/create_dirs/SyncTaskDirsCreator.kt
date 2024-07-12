package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.dirs_backuper.targetReadingStateIsOk
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files.notExistsInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import java.util.Collections
import javax.inject.Inject

/**
 * Создаёт каталоги, относящиеся к SyncTask-у.
 */
class SyncTaskDirsCreator @Inject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectDirCreatorCreator: SyncObjectDirCreatorCreator
){
    suspend fun createNewDirsFromTask(syncTask: SyncTask) {
        createSuppliedDirs("createNewDirsFromTask", syncTask) {
            syncObjectReader.getAllObjectsForTask(syncTask.id)
                .filter { it.isNew }
                .filter { it.isDir }
        }
    }


    suspend fun createNeverProcessedDirs(syncTask: SyncTask) {
        createSuppliedDirs("createNeverProcessedDirs", syncTask) {
            syncObjectReader.getAllObjectsForTask(syncTask.id)
                .filter { it.isDir }
                .filter { it.isNeverSynced }
                .filter { it.targetReadingStateIsOk }
        }
    }


    suspend fun createInTargetLostDirs(syncTask: SyncTask) {
        createSuppliedDirs("createInTargetLostDirs", syncTask) {
            syncObjectReader.getAllObjectsForTask(syncTask.id)
                .filter { it.isDir }
                .filter { it.targetReadingStateIsOk }
                .filter { it.notExistsInTarget }
        }
    }


    private suspend fun createSuppliedDirs(parentMethodName: String,
                                           syncTask: SyncTask,
                                           listSupplier: SuspendSupplier<List<SyncObject>>) {
        createDirsReal(syncTask, listSupplier.get(), parentMethodName)
    }


    private suspend fun createDirsReal(syncTask: SyncTask, dirList: List<SyncObject>, parentMethodName: String) {

        if (dirList.isEmpty())
            return

        Log.d(TAG, "createDirsReal('${parentMethodName}'), dirList: ${dirList.joinToString(", "){"'${it.name}'"}}")

        syncObjectDirCreatorCreator.createFor(syncTask)?.also { syncObjectDirCreator ->

            dirList.forEach { syncObject ->

                val objectInject = syncObject.id

                syncObjectStateChanger.setSyncState(objectInject, ExecutionState.RUNNING)

                syncObjectDirCreator.createDir(syncObject, syncTask)
                    .onSuccess {
                        syncObjectStateChanger.setSyncState(objectInject, ExecutionState.SUCCESS)
                    }
                    .onFailure { throwable ->
                        ExceptionUtils.getErrorMessage(throwable).also { errorMsg ->
                            syncObjectStateChanger.setSyncState(objectInject, ExecutionState.ERROR, errorMsg)
                            Log.e(TAG, errorMsg, throwable)
                        }
                    }
            }
        }
    }


    companion object {
        val TAG: String = SyncTaskDirsCreator::class.java.simpleName
    }
}


fun interface SuspendSupplier<T> {
    suspend fun get(): T
}


val SyncObject.isNeverSynced: Boolean get() = ExecutionState.NEVER == syncState
val SyncObject.isNew: Boolean get() = StateInSource.NEW == stateInSource