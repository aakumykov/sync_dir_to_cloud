package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingBegin
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingFailed
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingSuccess
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.CoroutineFileOperationJob
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.actualSize
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.helpers.ExecutionLoggerHelper
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncTaskFileObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_object_logger.SyncObjectLogger2
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch


/**
 * Выполняет копирование всех файловых SyncObject-ов указанного SyncTask,
 * меняя статус обрабатываемого SyncObject.
 */
class SyncTaskFilesCopier @AssistedInject constructor(
    @CoroutineFileOperationJob private val fileOperationJob: CompletableJob,
    private val syncTaskFileObjectReader: SyncTaskFileObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectFileCopierCreator: SyncObjectFileCopierCreator,
    private val syncObjectLogger2Factory: SyncObjectLogger2.Factory,
    private val executionLoggerHelper: ExecutionLoggerHelper,
    @Assisted private val executionId: String,
    @Assisted private val fileOperationPortionSize: Int,
) {
    fun copyNewFilesForSyncTaskInCoroutine(scope: CoroutineScope, syncTask: SyncTask): Job? {
        return scope.launch {
            syncTaskFileObjectReader
                .getNewFiles(syncTask.id)
                    ?.also { syncObjectList ->

                        Log.d(TAG, "copyNewFilesForSyncTaskInCoroutine()")

                        val operationName = R.string.SYNC_OBJECT_LOGGER_copy_new_file

                        copyFileListByChunksInCoroutine(
                            scope = scope,
                            syncTask = syncTask,
                            operationName = operationName,
                            list = syncObjectList,
                            overwriteIfExists = true,
                        ).join()
                    }
        }
    }

    fun copyPreviouslyForgottenFilesInCoroutine(scope: CoroutineScope, syncTask: SyncTask): Job? {
        return scope.launch {
            syncTaskFileObjectReader.getForgottenFiles(syncTask.id)
                ?.also { list ->

                    Log.d(TAG, "copyPreviouslyForgottenFilesInCoroutine()")

                    val operationName = R.string.SYNC_OBJECT_LOGGER_copy_previously_forgotten_file
//                    syncObjectLogger(syncTask.id).logWaiting(list, operationName)

                    copyFileListByChunksInCoroutine(
                        scope = scope,
                        syncTask = syncTask,
                        operationName = operationName,
                        list = list,
                        overwriteIfExists = true
                    )
                }
        }
    }

    fun copyModifiedFilesForSyncTask(scope: CoroutineScope, syncTask: SyncTask): Job? {
        return scope.launch {
            syncTaskFileObjectReader.getModifiedFiles(syncTask.id)
                ?.also {  list ->

                    Log.d(TAG, "copyModifiedFilesForSyncTask()")

                    //Log.d(TAG, "[${list.map { it.name }.joinToString(", ")}]")

//                    executionLoggerHelper.logStart(syncTask.id, executionId, R.string.EXECUTION_LOG_copying_modified_files)
                    val operationName = R.string.SYNC_OBJECT_LOGGER_copy_modified_file
//                    syncObjectLogger(syncTask.id).logWaiting(list, operationName)

                    copyFileListByChunksInCoroutine(
                        scope = scope,
                        syncTask = syncTask,
                        operationName = operationName,
                        list = list,
                        overwriteIfExists = true
                    )
                }
        }
        /*try {

                .also { list ->
                    if (list.isNotEmpty()) {

                    }
                }
        } catch (e: Exception) {
            executionLoggerHelper.logError(syncTask.id, executionId, TAG, e)
        }*/
    }

    fun copyInTargetLostFiles(scope: CoroutineScope, syncTask: SyncTask): Job? {
        return scope.launch {
            syncTaskFileObjectReader.getInTargetLostFiles(syncTask.id)
                ?.also { list ->

                    Log.d(TAG, "copyInTargetLostFiles()")

//                    executionLoggerHelper.logStart(syncTask.id,executionId,R.string.EXECUTION_LOG_copying_in_target_lost_files)
                    val operationName = R.string.SYNC_OBJECT_LOGGER_copy_in_target_lost_file
//                    syncObjectLogger(syncTask.id).logWaiting(list, operationName)

                    copyFileListByChunksInCoroutine(
                        scope = scope,
                        syncTask = syncTask,
                        operationName = operationName,
                        list = list,
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
    }


    // FIXME: добавить обработку ошибок
    private suspend fun copyFileListByChunksInCoroutine(
        scope: CoroutineScope,
        singleFileOperationJob: CompletableJob = fileOperationJob,
        @StringRes operationName: Int,
        list: List<SyncObject>,
        chunkSize: Int = fileOperationPortionSize,
        syncTask: SyncTask,
        overwriteIfExists: Boolean,
        onSyncObjectProcessingBegin: OnSyncObjectProcessingBegin? = null,
        onSyncObjectProcessingSuccess: OnSyncObjectProcessingSuccess? = null,
        onSyncObjectProcessingFailed: OnSyncObjectProcessingFailed? = null,
    ): Job {
        //Log.d(TAG, "copyFileListByChunksInCoroutine()")

        return scope.launch {
            list
                .chunked(chunkSize)
                .forEach { listChunk ->

                    //Log.d(TAG, "  chunk size: ${listChunk.size}")
                    syncObjectLogger(syncTask.id).logWaiting(listChunk, operationName)

                    copyFilesFromChunkInCoroutine(
                        scope = scope,
                        syncTask = syncTask,
                        operationName = operationName,
                        list = listChunk,
                        overwriteIfExists = overwriteIfExists,
                        singleFileOperationJob = singleFileOperationJob,
                        onSyncObjectProcessingBegin = onSyncObjectProcessingBegin,
                        onSyncObjectProcessingSuccess = onSyncObjectProcessingSuccess,
                        onSyncObjectProcessingFailed = onSyncObjectProcessingFailed,
                    ).join()
                }.also {
                    // Хак против остановки хода обработки после .joinAll()
                    //  https://stackoverflow.com/questions/66003458/how-to-correctly-join-all-jobs-launched-in-a-coroutinescope
                    singleFileOperationJob.complete()
                }
        }
    }


    private suspend fun copyFileListReal(
        operationName: Int,
        list: List<SyncObject>,
        syncTask: SyncTask,
        overwriteIfExists: Boolean,
        onSyncObjectProcessingBegin: (suspend (syncObject: SyncObject) -> Unit)?,
        onSyncObjectProcessingSuccess: (suspend (syncObject: SyncObject) -> Unit)?,
        onSyncObjectProcessingFailed: (suspend (syncObject: SyncObject, throwable: Throwable) -> Unit)?
    ) {
        //Log.d(TAG, "copyFileListReal(${list.size})")

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
                    //Log.d(TAG, "syncObject: id=${syncObject.id}, name=${syncObject.name}, progress: ${progressAsPartOf100}")
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


    // FIXME: добавить обработку ошибок
    private suspend fun copyFilesFromChunkInCoroutine(
        list: List<SyncObject>,
        operationName: Int,
        syncTask: SyncTask,
        overwriteIfExists: Boolean,
        scope: CoroutineScope,
        singleFileOperationJob: CompletableJob,
        onSyncObjectProcessingBegin: (suspend (syncObject: SyncObject) -> Unit)?,
        onSyncObjectProcessingSuccess: (suspend (syncObject: SyncObject) -> Unit)?,
        onSyncObjectProcessingFailed: (suspend (syncObject: SyncObject, throwable: Throwable) -> Unit)?
    ): Job {
        //Log.d(TAG, "copyFilesRealInCoroutine(), list size: ${list.size}")

        return scope.launch {

            //Log.d(TAG, "--------------- старт ----------------")

            val syncObjectCopier: StreamToFileDataCopier?
                = syncObjectFileCopierCreator.createFileCopierFor(syncTask)

            list.map { syncObject: SyncObject ->
                //Log.d(TAG, "list.map{ ${syncObject.name} }")
                launch (singleFileOperationJob) {
                    //Log.d(TAG, "launch{ ${syncObject.name} }")
                    processSingleFile(
                        syncObjectCopier = syncObjectCopier,
                        syncTask = syncTask,
                        syncObject = syncObject,
                        operationName = operationName,
                        overwriteIfExists = overwriteIfExists,
                        onSyncObjectProcessingBegin = onSyncObjectProcessingBegin,
                        onSyncObjectProcessingSuccess = onSyncObjectProcessingSuccess,
                        onSyncObjectProcessingFailed = onSyncObjectProcessingFailed,
                    )
                }
            }.joinAll()

            //Log.d(TAG, "--------------- финиш ----------------")
        }
    }


    private suspend fun processSingleFile(
        operationName: Int,
        syncObject: SyncObject,
        overwriteIfExists: Boolean,
        syncTask: SyncTask,
        syncObjectCopier: StreamToFileDataCopier?,
        onSyncObjectProcessingBegin: (suspend (syncObject: SyncObject) -> Unit)?,
        onSyncObjectProcessingSuccess: (suspend (syncObject: SyncObject) -> Unit)?,
        onSyncObjectProcessingFailed: (suspend (syncObject: SyncObject, throwable: Throwable) -> Unit)?
    ) {
        //Log.d(TAG, "processSingleFile(): id=${syncObject.id}, name=${syncObject.name})")

        val objectId = syncObject.id

        syncObjectStateChanger.setSyncState(objectId, ExecutionState.RUNNING)

        // FIXME: избавиться от "!!"
        val sourcePath = syncObject.absolutePathIn(syncTask.sourcePath!!)
        val targetPath = syncObject.absolutePathIn(syncTask.targetPath!!)

        val progressCalculator = ProgressCalculator(syncObject.actualSize)

        onSyncObjectProcessingBegin?.invoke(syncObject)

        syncObjectCopier
            ?.copyDataFromPathToPath(
                absoluteSourceFilePath = sourcePath,
                absoluteTargetFilePath = targetPath,
                overwriteIfExists = overwriteIfExists,
                progressCalculator = progressCalculator
            ) { progressAsPartOf100: Int ->
//                //Log.d(TAG, "syncObject: id=${syncObject.id}, name=${syncObject.name}, progress: ${progressAsPartOf100}")
                syncObjectLogger(syncTask.id).logProgress(syncObject.id, syncTask.id, executionId, progressAsPartOf100)
            }?.onSuccess {
                syncObjectStateChanger.markAsSuccessfullySynced(objectId)
                syncObjectLogger(syncTask.id).logSuccess(syncObject, operationName)
                onSyncObjectProcessingSuccess?.invoke(syncObject)
            }
            ?.onFailure { throwable ->
                ExceptionUtils.getErrorMessage(throwable).also { errorMsg ->
                    Log.e(TAG, "${syncObject.name}: $errorMsg")
                    syncObjectStateChanger.setSyncState(objectId, ExecutionState.ERROR, errorMsg)
                    syncObjectLogger(syncTask.id).logFail(syncObject, operationName, errorMsg)
                    onSyncObjectProcessingFailed?.invoke(syncObject, throwable)
                }
            }
    }


    // TODO: внедрять как @Assisted
    private fun syncObjectLogger(taskId: String): SyncObjectLogger2 {
        return syncObjectLogger2Factory.create(taskId, executionId)
    }


    companion object {
        val TAG: String = SyncTaskFilesCopier::class.java.simpleName
    }
}
