package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingBegin
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingFailed
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingSuccess
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.WrappedSyncObject
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.file_copier.createOperationId
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.CoroutineFileCopyingScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
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
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.helpers.ExecutionLoggerHelper
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_object_logger.SyncObjectLogger2
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.CancellationException


/**
 * Выполняет копирование всех файловых SyncObject-ов указанного SyncTask,
 * меняя статус обрабатываемого SyncObject.
 */
class SyncTaskFilesCopier @AssistedInject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectDataCopierCreator: SyncObjectDataCopierCreator,
    private val syncObjectLogger2Factory: SyncObjectLogger2.Factory,
    private val executionLoggerHelper: ExecutionLoggerHelper,
    private val operationCancellationHolder: OperationCancellationHolder,
    @CoroutineFileCopyingScope private val fileCopyingScope: CoroutineScope,
    @DispatcherIO private val fileCopyingDispatcher: CoroutineDispatcher,
    @Assisted private val executionId: String,
) {
    suspend fun copyNewFilesForSyncTask(syncTask: SyncTask): Job? {

        val operationId = createOperationId()

        return try {
            syncObjectReader
                .getAllObjectsForTask(syncTask.id)
                .filter { it.isFile }
                .filter { it.isNew }
                .let { list ->
                    if (list.isNotEmpty()) {

                        val operationName = R.string.SYNC_OBJECT_LOGGER_copy_new_file

                        val wrappedList = WrappedSyncObject.wrapList(list, operationId)

                        logWaiting(
                            taskId = syncTask.id,
                            operationName = operationName,
                            list = wrappedList,
                        )

                        copyFilesReal(
                            operationName = operationName,
                            list = wrappedList,
                            syncTask = syncTask,
                            overwriteIfExists = true
                        )
                    } else {
                        null
                    }
                }
        } catch (e: Exception) {
            executionLoggerHelper.logError(syncTask.id, executionId, operationId, TAG, e)
            null
        }
    }


    suspend fun copyPreviouslyForgottenFilesOfSyncTask(syncTask: SyncTask): Job? {

        val operationId = createOperationId()

        return try {
            syncObjectReader
                .getAllObjectsForTask(syncTask.id)
                .filter { it.isFile }
                .filter { it.isNeverSynced }
                .let { list ->
                    if (list.isNotEmpty()) {

                        val operationName = R.string.SYNC_OBJECT_LOGGER_copy_previously_forgotten_file

                        val wrappedList = WrappedSyncObject.wrapList(list, operationId)

                        logWaiting(
                            taskId = syncTask.id,
                            operationName = operationName,
                            list = wrappedList,
                        )

                        copyFilesReal(
                            operationName = operationName,
                            list = wrappedList,
                            syncTask = syncTask,
                            overwriteIfExists = true
                        )
                    } else {
                        null
                    }
                }
        } catch (e: Exception) {
            executionLoggerHelper.logError(syncTask.id, executionId, operationId, TAG, e)
            null
        }
    }


    suspend fun copyModifiedFilesForSyncTask(syncTask: SyncTask): Job? {

        val operationId = createOperationId()

        return try {
            syncObjectReader
                .getAllObjectsForTask(syncTask.id)
                .filter { it.isFile }
                .filter { it.isModified }
                .filter { it.isTargetReadingOk }
                .let { list ->
                    if (list.isNotEmpty()) {

                        val operationName = R.string.SYNC_OBJECT_LOGGER_copy_modified_file

                        val wrappedList = WrappedSyncObject.wrapList(list, operationId)

                        logWaiting(syncTask.id, operationName, wrappedList)

                        copyFilesReal(
                            operationName = operationName,
                            list = wrappedList,
                            syncTask = syncTask,
                            overwriteIfExists = true
                        )
                    } else {
                        null
                    }
                }
        } catch (e: Exception) {
            executionLoggerHelper.logError(syncTask.id, executionId, operationId, TAG, e)
            null
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

                        val operationName = R.string.SYNC_OBJECT_LOGGER_copy_in_target_lost_file
                        val wrappedList = WrappedSyncObject.wrapList(list, operationId)

                        logWaiting(
                            taskId = syncTask.id,
                            operationName = operationName,
                            list =wrappedList
                        )

                        copyFilesReal(
                            operationName = operationName,
                            list = wrappedList,
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
        list: List<WrappedSyncObject>,
        syncTask: SyncTask,
        overwriteIfExists: Boolean,
        onSyncObjectProcessingBegin: OnSyncObjectProcessingBegin? = null,
        onSyncObjectProcessingSuccess: OnSyncObjectProcessingSuccess? = null,
        onSyncObjectProcessingFailed: OnSyncObjectProcessingFailed? = null,
    ): Job {
        return fileCopyingScope.launch {

            val syncObjectDataCopier = syncObjectDataCopierCreator.createDataCopierFor(syncTask)

            val jobsList: MutableList<Job> = mutableListOf()

            list.forEach { wrappedSyncObject ->

                val syncObject = wrappedSyncObject.syncObject
                val objectId = syncObject.id
                val operationId = wrappedSyncObject.operationId

                syncObjectStateChanger.setSyncState(objectId, ExecutionState.RUNNING)

                onSyncObjectProcessingBegin?.invoke(syncObject)

                // FIXME: избавиться от "!!"
                val sourcePath = syncObject.absolutePathIn(syncTask.sourcePath!!)
                val targetPath = syncObject.absolutePathIn(syncTask.targetPath!!)

                val progressCalculator = ProgressCalculator(syncObject.actualSize)

                val fileCopyingJob = fileCopyingScope.launch (fileCopyingDispatcher) {
                    try {
                        syncObjectDataCopier?.copyDataFromPathToPath(
                            absoluteSourceFilePath = sourcePath,
                            absoluteTargetFilePath = targetPath,
                            overwriteIfExists = overwriteIfExists,
                            progressCalculator = progressCalculator
                        ) { progressAsPartOf100 ->
                            delay(1000)
                            syncObjectLogger(syncTask.id).logProgress(
                                syncObject.id,
                                syncTask.id,
                                executionId,
                                progressAsPartOf100
                            )
                        }
                        syncObjectStateChanger.markAsSuccessfullySynced(objectId)
                        onSyncObjectProcessingSuccess?.invoke(syncObject)
                        syncObjectLogger(syncTask.id).logSuccess(syncObject, operationName)

                    } catch (e: CancellationException) {
                        Log.d(TAG, e.errorMsg)

                    } catch (e: Exception) {
                        ExceptionUtils.getErrorMessage(e).also { errorMsg ->
                            syncObjectStateChanger.setSyncState(objectId, ExecutionState.ERROR, errorMsg)
                            onSyncObjectProcessingFailed?.invoke(syncObject, e) ?: Log.e(TAG, errorMsg, e)
                            syncObjectLogger(syncTask.id).logFail(syncObject, operationName, operationId, errorMsg)
                        }
                    }
                }

                operationCancellationHolder.addJob(
                    operationId = operationId,
                    job = fileCopyingJob,
                )

                jobsList.add(fileCopyingJob)

                /*syncObjectCopier
                    ?.copyDataFromPathToPath(
                        absoluteSourceFilePath = sourcePath,
                        absoluteTargetFilePath = targetPath,
                        overwriteIfExists = overwriteIfExists,
                        progressCalculator = progressCalculator
                    ) { progressAsPartOf100: Int ->
                        syncObjectLogger(syncTask.id).logProgress(
                            syncObject.id,
                            syncTask.id,
                            executionId,
                            progressAsPartOf100
                        )
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
                            syncObjectLogger(syncTask.id).logFail(syncObject, operationName, operationId, errorMsg)
                        }
                    }*/
            }

            jobsList.onEach { it.join() }
        }
    }


    private suspend fun logWaiting(taskId: String, operationName: Int, list: List<WrappedSyncObject>) {
        syncObjectLogger(taskId).apply {
            list.forEach { wrappedSyncObject ->
                logWaiting(
                    syncObject = wrappedSyncObject.syncObject,
                    operationName = operationName,
                    operationId = wrappedSyncObject.operationId,
                )
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
