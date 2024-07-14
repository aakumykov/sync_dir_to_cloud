package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingBegin
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingFailed
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingSuccess
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs.names
import com.github.aakumykov.sync_dir_to_cloud.config.CloudType.Companion.list
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isModified
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNeverSynced
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNew
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.notExistsInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isTargetReadingOk
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
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
            .getAllObjectsForTask(syncTask.id)
            .filter { it.isFile }
            .filter { it.isNew }
            .also { list ->
                if (list.isNotEmpty()) Log.d(TAG + "_" + SyncTaskExecutor.TAG, "copyNewFilesForSyncTask(${list.names})")
                copyFiles(
                    list = list,
                    syncTask = syncTask,
                    overwriteIfExists = true
                )
        }
    }

    suspend fun copyNeverCopiedFilesOfSyncTask(syncTask: SyncTask) {
        syncObjectReader
            .getAllObjectsForTask(syncTask.id)
            .filter { it.isFile }
            .filter { it.isNeverSynced }
            .also { list ->
                if (list.isNotEmpty()) Log.d(TAG + "_" + SyncTaskExecutor.TAG, "copyNeverCopiedFilesOfSyncTask(${list.names})")
                copyFiles(
                    list = list,
                    syncTask = syncTask,
                    overwriteIfExists = true
                )
            }
    }

    suspend fun copyModifiedFilesForSyncTask(syncTask: SyncTask) {
        syncObjectReader
            .getAllObjectsForTask(syncTask.id)
            .filter { it.isFile }
            .filter { it.isModified }
            .filter { it.isTargetReadingOk }
            .also { list ->
                if (list.isNotEmpty()) Log.d(TAG + "_" + SyncTaskExecutor.TAG, "copyModifiedFilesForSyncTask(${list.names})")
                copyFiles(
                    list = list,
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
            .filter { it.isTargetReadingOk }
            .also { list ->
                if (list.isNotEmpty()) Log.d(TAG + "_" + SyncTaskExecutor.TAG, "copyInTargetLostFiles(${list.names})")
                copyFiles(
                    list = list,
                    syncTask = syncTask,
                    overwriteIfExists = false,
                    onSyncObjectProcessingBegin = { syncObject ->
                        syncObjectStateChanger.setRestorationState(syncObject.id, ExecutionState.RUNNING)
                    },
                    onSyncObjectProcessingSuccess = { syncObject ->
                        syncObjectStateChanger.setRestorationState(syncObject.id, ExecutionState.SUCCESS)
                    },
                    onSyncObjectProcessingFailed = { syncObject, throwable ->
                        ExceptionUtils.getErrorMessage(throwable).also { errorMsg ->
                            syncObjectStateChanger.setRestorationState(syncObject.id, ExecutionState.ERROR, errorMsg)
                            Log.e(TAG, errorMsg, throwable)
                        }
                    }
                )
            }
    }


    private suspend fun copyFiles(
        list: List<SyncObject>,
        syncTask: SyncTask,
        overwriteIfExists: Boolean,
        onSyncObjectProcessingBegin: OnSyncObjectProcessingBegin? = null,
        onSyncObjectProcessingSuccess: OnSyncObjectProcessingSuccess? = null,
        onSyncObjectProcessingFailed: OnSyncObjectProcessingFailed? = null,
    ) {
        val syncObjectCopier = syncObjectCopierCreator.createFileCopierFor(syncTask)

        list.forEach { syncObject ->

            val objectId = syncObject.id

            syncObjectStateChanger.setSyncState(objectId, ExecutionState.RUNNING)
            onSyncObjectProcessingBegin?.invoke(syncObject)

            syncObjectCopier
                ?.copySyncObject(syncObject, syncTask, overwriteIfExists)
                ?.onSuccess {
                    syncObjectStateChanger.setSyncState(objectId, ExecutionState.SUCCESS)
                    onSyncObjectProcessingSuccess?.invoke(syncObject)
                }
                ?.onFailure { throwable ->
                    ExceptionUtils.getErrorMessage(throwable).also { errorMsg ->
                        syncObjectStateChanger.setSyncState(objectId, ExecutionState.ERROR, errorMsg)
                        onSyncObjectProcessingFailed?.invoke(syncObject, throwable)
                            ?: Log.e(TAG, errorMsg, throwable)
                    }
                }
        }
    }

    companion object {
        val TAG: String = SyncTaskFilesCopier::class.java.simpleName
    }
}