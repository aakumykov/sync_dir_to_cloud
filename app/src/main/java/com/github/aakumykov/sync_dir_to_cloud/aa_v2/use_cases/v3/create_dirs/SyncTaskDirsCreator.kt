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
import javax.inject.Inject


typealias OnSyncObjectProcessingBegin = suspend (syncObject: SyncObject) -> Unit
typealias OnSyncObjectProcessingSuccess = suspend (syncObject: SyncObject) -> Unit
typealias OnSyncObjectProcessingFailed = suspend (syncObject: SyncObject, throwable: Throwable) -> Unit

/**
 * Создаёт каталоги, относящиеся к SyncTask-у.
 */
class SyncTaskDirsCreator @Inject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectDirCreatorCreator: SyncObjectDirCreatorCreator
){
    suspend fun createNewDirsFromTask(syncTask: SyncTask) {
        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isDir }
            .filter { it.isNew }
            .also { list ->
                createDirs(
                    parentMethodName = "createNewDirsFromTask",
                    dirList = list,
                    syncTask = syncTask,
                )
            }
    }

    // SyncState = NEVER && StateInSource == UNCHANGED
    suspend fun createNeverProcessedDirs(syncTask: SyncTask) {
        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isDir }
            .filter { it.isNeverSynced && it.isUnchanged }
            .filter { it.targetReadingStateIsOk }
            .also { list ->
                createDirs(
                    parentMethodName = "createNeverProcessedDirs",
                    dirList = list,
                    syncTask = syncTask,
                )
            }
    }


    // isExistsInTarget == false
    suspend fun createInTargetLostDirs(syncTask: SyncTask) {
         syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isDir }
            .filter { it.targetReadingStateIsOk }
            .filter { it.notExistsInTarget }
            .also { list ->
                createDirs(
                    parentMethodName = "createInTargetLostDirs",
                    dirList = list,
                    syncTask = syncTask,
                    onSyncObjectProcessingBegin = { syncObject ->
                        syncObjectStateChanger.setRestorationState(syncObject.id, ExecutionState.RUNNING)
                    },
                    onSyncObjectProcessingSuccess = { syncObject ->
                        syncObjectStateChanger.setRestorationState(syncObject.id, ExecutionState.SUCCESS)
                    },
                    onSyncObjectProcessingFailed = { syncObject, throwable ->
                        ExceptionUtils.getErrorMessage(throwable).also { errorMsg ->
                            syncObjectStateChanger.setRestorationState(syncObject.id, ExecutionState.RUNNING, errorMsg)
                            Log.e(TAG, errorMsg, throwable)
                        }
                    }
                )
            }
    }


    private suspend fun createDirs(
        parentMethodName: String,
        dirList: List<SyncObject>,
        syncTask: SyncTask,
        onSyncObjectProcessingBegin: OnSyncObjectProcessingBegin? = null,
        onSyncObjectProcessingSuccess: OnSyncObjectProcessingSuccess? = null,
        onSyncObjectProcessingFailed: OnSyncObjectProcessingFailed? = null,
    ) {
            if (dirList.isNotEmpty()) {

                Log.d(TAG, "createDirsReal('${parentMethodName}'), dirList: ${dirList.joinToString(", "){"'${it.name}'"}}")

                syncObjectDirCreatorCreator.createFor(syncTask)?.also { syncObjectDirCreator ->

                    dirList.forEach { syncObject ->

                        val objectId = syncObject.id

                        syncObjectStateChanger.setSyncState(objectId, ExecutionState.RUNNING)
                        onSyncObjectProcessingBegin?.invoke(syncObject)

                        syncObjectDirCreator.createDir(syncObject, syncTask)
                            .onSuccess {
                                syncObjectStateChanger.setSyncState(objectId, ExecutionState.SUCCESS)
                                onSyncObjectProcessingSuccess?.invoke(syncObject)
                            }
                            .onFailure { throwable ->
                                ExceptionUtils.getErrorMessage(throwable).also { errorMsg ->
                                    syncObjectStateChanger.setSyncState(objectId, ExecutionState.ERROR, errorMsg)
                                    Log.e(TAG, errorMsg, throwable)
                                }
                                onSyncObjectProcessingFailed?.invoke(syncObject, throwable)
                            }
                    }
                }
            }
    }


    companion object {
        val TAG: String = SyncTaskDirsCreator::class.java.simpleName
    }
}


val SyncObject.isNeverSynced: Boolean get() = ExecutionState.NEVER == syncState
val SyncObject.isUnchanged: Boolean get() = StateInSource.UNCHANGED == stateInSource
val SyncObject.isNew: Boolean get() = StateInSource.NEW == stateInSource