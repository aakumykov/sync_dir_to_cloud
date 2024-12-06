package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingBegin
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingFailed
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingSuccess
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.actualSize
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isModified
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNeverSynced
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNew
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isSuccessSynced
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.notExistsInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isTargetReadingOk
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.helpers.ExecutionLoggerHelper
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_object_logger.SyncObjectLogger2
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID


/**
 * Выполняет копирование всех файловых SyncObject-ов указанного SyncTask,
 * меняя статус обрабатываемого SyncObject.
 */
class SyncTaskFilesCopier @AssistedInject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectFileCopierCreator: SyncObjectFileCopierCreator,
    private val syncObjectLogger2Factory: SyncObjectLogger2.Factory,
    private val executionLoggerHelper: ExecutionLoggerHelper,
    @Assisted private val executionId: String,
) {
    suspend fun copyNewFilesForSyncTask(syncTask: SyncTask) {

        val operationId = UUID.randomUUID().toString()

        try {
            syncObjectReader
                .getAllObjectsForTask(syncTask.id)
                .filter { it.isFile }
                .filter { it.isNew }
                .also { list ->
                    if (list.isNotEmpty()) {
                        // Выводить сообщение "Копирую новые файлы" не нужно,
                        // так как будет сообщение для каждого файла отдельно.
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
        } catch (e: Exception) {
            executionLoggerHelper.logError(syncTask.id, executionId, operationId, TAG, e)
        }
    }

    suspend fun copyPreviouslyForgottenFilesOfSyncTask(syncTask: SyncTask) {

        val operationId = UUID.randomUUID().toString()

        try {
            syncObjectReader
                .getAllObjectsForTask(syncTask.id)
                .filter { it.isFile }
                .filter { it.isNeverSynced }
                .also { list ->
                    if (list.isNotEmpty()) {
//                        executionLoggerHelper.logStart(syncTask.id, executionId, R.string.EXECUTION_LOG_copying_previously_forgotten_files)
                        val operationName = R.string.SYNC_OBJECT_LOGGER_copy_previously_forgotten_file
                        syncObjectLogger(syncTask.id).logWaiting(list, operationName)
                        copyFilesReal(
                            operationName = operationName,
                            list = list,
                            syncTask = syncTask,
                            overwriteIfExists = true
                        )
                    }
                }
        } catch (e: Exception) {
            executionLoggerHelper.logError(syncTask.id, executionId, operationId, TAG, e)
        }
    }

    suspend fun copyModifiedFilesForSyncTask(syncTask: SyncTask) {

        val operationId = UUID.randomUUID().toString()

        try {
            syncObjectReader
                .getAllObjectsForTask(syncTask.id)
                .filter { it.isFile }
                .filter { it.isModified }
                .filter { it.isTargetReadingOk }
                .also { list ->
                    if (list.isNotEmpty()) {
//                        executionLoggerHelper.logStart(syncTask.id, executionId, R.string.EXECUTION_LOG_copying_modified_files)
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
        } catch (e: Exception) {
            executionLoggerHelper.logError(syncTask.id, executionId, operationId, TAG, e)
        }
    }

    suspend fun copyInTargetLostFiles(syncTask: SyncTask) {

        val operationId = UUID.randomUUID().toString()

        try {
            syncObjectReader
                .getAllObjectsForTask(syncTask.id)
                .filter { it.isFile }
                .filter { it.isSuccessSynced }
                .filter { it.notExistsInTarget }
                .filter { it.isTargetReadingOk }
                .also { list ->
                    if (list.isNotEmpty()) {
//                        executionLoggerHelper.logStart(syncTask.id,executionId,R.string.EXECUTION_LOG_copying_in_target_lost_files)
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
        } catch (e: Exception) {
            executionLoggerHelper.logError(syncTask.id, executionId, operationId, TAG, e)
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
        val syncObjectCopier = syncObjectFileCopierCreator.createFileCopierFor(syncTask)

        list.forEach { syncObject ->

            val objectId = syncObject.id

            syncObjectStateChanger.setSyncState(objectId, ExecutionState.RUNNING)
            onSyncObjectProcessingBegin?.invoke(syncObject)

            // FIXME: избавиться от "!!"
            val sourcePath = syncObject.absolutePathIn(syncTask.sourcePath!!)
            val targetPath = syncObject.absolutePathIn(syncTask.targetPath!!)

            val progressCalculator = ProgressCalculator(syncObject.actualSize)

            syncObjectCopier
                ?.copyDataFromPathToPath(
                    absoluteSourceFilePath = sourcePath,
                    absoluteTargetFilePath = targetPath,
                    overwriteIfExists = overwriteIfExists,
                    progressCalculator = progressCalculator
                ) { progressAsPartOf100: Int ->
                    syncObjectLogger(syncTask.id)
                        .logProgress(syncObject.id, syncTask.id, executionId, progressAsPartOf100)
                }
                ?.onSuccess {
                    syncObjectStateChanger.markAsSuccessfullySynced(objectId)
                    onSyncObjectProcessingSuccess?.invoke(syncObject)
                    syncObjectLogger(syncTask.id).logSuccess(syncObject, operationName)
                }
                ?.onFailure { throwable ->
                    ExceptionUtils.getErrorMessage(throwable).also { errorMsg ->
                        syncObjectStateChanger.setSyncState(objectId, ExecutionState.ERROR, errorMsg)
                        onSyncObjectProcessingFailed?.invoke(syncObject, throwable) ?: Log.e(TAG, errorMsg, throwable)
                        syncObjectLogger(syncTask.id).logFail(syncObject, operationName, errorMsg)
                    }
                }
        }
    }


    private fun syncObjectLogger(taskId: String): SyncObjectLogger2 {
        return syncObjectLogger2Factory.create(taskId, executionId)
    }


    companion object {
        val TAG: String = SyncTaskFilesCopier::class.java.simpleName
    }
}
