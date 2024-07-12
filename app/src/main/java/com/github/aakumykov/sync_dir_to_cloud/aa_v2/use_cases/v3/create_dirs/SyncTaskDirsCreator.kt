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

/**
 * Создаёт каталоги, относящиеся к SyncTask-у.
 */
class SyncTaskDirsCreator @Inject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectDirCreatorCreator: SyncObjectDirCreatorCreator
){
    suspend fun createNewDirsFromTask(syncTask: SyncTask) {
        syncObjectReader.getObjectsForTaskWithModificationState(syncTask.id, StateInSource.NEW)
            .filter { it.isDir }
            .also { list -> createDirsReal(syncTask, list) }
    }


    suspend fun createNeverProcessedDirsFromTask(syncTask: SyncTask) {
        createSuppliedDirs(syncTask) {
            syncObjectReader.getAllObjectsForTask(syncTask.id)
                .filter { it.isDir }
                .filter { it.syncState == ExecutionState.NEVER }
        }
    }


    suspend fun createInTargetLostDirs(syncTask: SyncTask) {
        createSuppliedDirs(syncTask) {
            syncObjectReader.getAllObjectsForTask(syncTask.id)
                .filter { it.isDir }
                .filter { it.targetReadingStateIsOk }
                .filter { it.notExistsInTarget }
        }
    }


    private suspend fun createSuppliedDirs(syncTask: SyncTask, listSupplier: SuspendSupplier<List<SyncObject>>) {
        createDirsReal(syncTask, listSupplier.get())
    }


    private suspend fun createDirsReal(syncTask: SyncTask, dirList: List<SyncObject>) {

        syncObjectDirCreatorCreator.createFor(syncTask)?.also { syncObjectDirCreator ->

            dirList.forEach { syncObject ->

                val objectInject = syncObject.id

                syncObjectStateChanger.setRestorationState(objectInject, ExecutionState.RUNNING)

                syncObjectDirCreator.createDir(syncObject, syncTask)
                    .onSuccess {
                        syncObjectStateChanger.setRestorationState(objectInject, ExecutionState.SUCCESS)
                    }
                    .onFailure { throwable ->
                        ExceptionUtils.getErrorMessage(throwable).also { errorMsg ->
                            syncObjectStateChanger.setRestorationState(objectInject, ExecutionState.ERROR, errorMsg)
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