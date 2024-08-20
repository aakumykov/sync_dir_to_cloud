package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingBegin
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingFailed
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingSuccess
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isModified
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNeverSynced
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNew
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isSuccessSynced
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.notExistsInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isTargetReadingOk
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_object_logger.SyncObjectLogger2
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Выполняет копирование всех файловых SyncObject-ов указанного SyncTask,
 * меняя статус обрабатываемого SyncObject.
 */
class SyncTaskFilesCopier @AssistedInject constructor(
    private val resources: Resources,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectCopierCreator: SyncObjectCopierCreator,
    private val syncObjectLogger2Factory: SyncObjectLogger2.Factory,
    @Assisted private val executionId: String,
) {
    private fun syncObjectLogger(taskId: String): SyncObjectLogger2 {
        return syncObjectLogger2Factory.create(taskId, executionId)
    }

    suspend fun copyNewFilesForSyncTask(syncTask: SyncTask) {
        syncObjectReader
            .getAllObjectsForTask(syncTask.id)
            .filter { it.isFile }
            .filter { it.isNew }
            .also { list ->

                val operationName = R.string.SYNC_OBJECT_LOGGER_copy_new_file

                syncObjectLogger(syncTask.id).logWaiting(list, operationName)

                copyFilesReal(
                    operationName = operationName,
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

                val operationName = R.string.SYNC_OBJECT_LOGGER_copy_never_copied_file

                syncObjectLogger(syncTask.id).logWaiting(list, operationName)

                copyFilesReal(
                    operationName = operationName,
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

                val operationName = R.string.SYNC_OBJECT_LOGGER_copy_modified_file

                syncObjectLogger(syncTask.id).logWaiting(list, operationName)

                copyFilesReal(
                    operationName = operationName,
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
            .filter { it.isSuccessSynced }
            .filter { it.notExistsInTarget }
            .filter { it.isTargetReadingOk }
            .also { list ->

                val operationName = R.string.SYNC_OBJECT_LOGGER_copy_in_target_lost_file

                syncObjectLogger(syncTask.id).logWaiting(list, operationName)

                copyFilesReal(
                    operationName = operationName,
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


    private suspend fun copyFilesReal(
        @StringRes operationName: Int,
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
                    syncObjectStateChanger.markAsSuccessfullySynced(objectId)
                    onSyncObjectProcessingSuccess?.invoke(syncObject)
                    syncObjectLogger(syncTask.id).logSuccess(syncObject, operationName)
                }
                ?.onFailure { throwable ->
                    ExceptionUtils.getErrorMessage(throwable).also { errorMsg ->
                        syncObjectStateChanger.setSyncState(objectId, ExecutionState.ERROR, errorMsg)
                        onSyncObjectProcessingFailed?.invoke(syncObject, throwable) ?: Log.e(TAG, errorMsg, throwable)
                        /*syncObjectLogger.log(SyncObjectLogItem.createFailed(
                            taskId = syncTask.id,
                            executionId = executionId,
                            syncObject = syncObject,
                            operationName = getString(operationName),
                            errorMessage = errorMsg
                        ))*/
                        syncObjectLogger(syncTask.id).logFail(syncObject, operationName, errorMsg)
                    }
                }
        }
    }

    private fun getString(@StringRes stringRes: Int): String = resources.getString(stringRes)

    private fun getString(@StringRes stringRes: Int, vararg arguments: Any) = resources.getString(stringRes, arguments)


    companion object {
        val TAG: String = SyncTaskFilesCopier::class.java.simpleName
    }
}

@AssistedFactory
interface SyncTaskFilesCopierAssistedFactory {
    fun create(executionId: String): SyncTaskFilesCopier
}