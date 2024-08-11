package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingBegin
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingFailed
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.OnSyncObjectProcessingSuccess
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNeverSynced
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNew
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isSuccessSynced
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isTargetReadingOk
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isUnchanged
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.notExistsInTarget
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_object_logger.SyncObjectLogger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject


/**
 * Создаёт каталоги, относящиеся к SyncTask-у.
 */
class SyncTaskDirsCreator @AssistedInject constructor(
    private val resources: Resources,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectDirCreatorCreator: SyncObjectDirCreatorCreator,
    @Assisted private val syncObjectLogger: SyncObjectLogger
){
    suspend fun createNewDirs(syncTask: SyncTask, executionId: String) {
        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isDir }
            .filter { it.isNew }
            .also { list ->
                if (list.isNotEmpty()) Log.d(TAG + "_" + SyncTaskExecutor.TAG, "createNewDirs(${list.names})")
                createDirs(
                    operationName = R.string.SYNC_OBJECT_LOGGER_create_new_dir,
                    dirList = list,
                    syncTask = syncTask,
                    executionId = executionId,
                )
            }
    }

    // SyncState = NEVER && StateInSource == UNCHANGED
    suspend fun createNeverProcessedDirs(syncTask: SyncTask, executionId: String) {
        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isDir }
            .filter { it.isNeverSynced && it.isUnchanged }
            .filter { it.isTargetReadingOk }
            .also { list ->
                if (list.isNotEmpty()) Log.d(TAG + "_" + SyncTaskExecutor.TAG, "createNeverProcessedDirs(${list.names})")
                createDirs(
                    operationName = R.string.SYNC_OBJECT_LOGGER_create_never_processed_dir,
                    dirList = list,
                    syncTask = syncTask,
                    executionId = executionId,
                )
            }
    }


    // isExistsInTarget == false
    suspend fun createInTargetLostDirs(syncTask: SyncTask, executionId: String) {
         syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isDir }
            .filter { it.isSuccessSynced }
            .filter { it.notExistsInTarget }
            .filter { it.isTargetReadingOk }
            .also { list ->
                if (list.isNotEmpty()) Log.d(TAG + "_" + SyncTaskExecutor.TAG, "createInTargetLostDirs(${list.names})")
                createDirs(
                    operationName = R.string.SYNC_OBJECT_LOGGER_create_in_target_lost_dir,
                    dirList = list,
                    syncTask = syncTask,
                    executionId = executionId,
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
        @StringRes operationName: Int,
        dirList: List<SyncObject>,
        syncTask: SyncTask,
        executionId: String,
        onSyncObjectProcessingBegin: OnSyncObjectProcessingBegin? = null,
        onSyncObjectProcessingSuccess: OnSyncObjectProcessingSuccess? = null,
        onSyncObjectProcessingFailed: OnSyncObjectProcessingFailed? = null,
    ) {
            if (dirList.isNotEmpty()) {

//                Log.d(TAG+"_"+SyncTaskExecutor.TAG, "createDirsReal('${parentMethodName}'), dirList: ${dirList.joinToString(", "){"'${it.name}'"}}")

                syncObjectDirCreatorCreator.createFor(syncTask)?.also { syncObjectDirCreator ->

                    dirList.forEach { syncObject ->

                        val objectId = syncObject.id

                        syncObjectStateChanger.setSyncState(objectId, ExecutionState.RUNNING)
                        onSyncObjectProcessingBegin?.invoke(syncObject)

                        syncObjectDirCreator.createDir(syncObject, syncTask)
                            .onSuccess {
                                syncObjectStateChanger.markAsSuccessfullySynced(objectId)

                                onSyncObjectProcessingSuccess?.invoke(syncObject)

                                syncObjectLogger.log(SyncObjectLogItem.createSuccess(
                                    taskId = syncTask.id,
                                    executionId = executionId,
                                    syncObject,
                                    message = getString(operationName)
                                ))
                            }
                            .onFailure { throwable ->
                                ExceptionUtils.getErrorMessage(throwable).also { errorMsg ->

                                    syncObjectStateChanger.setSyncState(objectId, ExecutionState.ERROR, errorMsg)

                                    onSyncObjectProcessingFailed?.invoke(syncObject, throwable) ?: Log.e(TAG, errorMsg, throwable)

                                    syncObjectLogger.log(SyncObjectLogItem.createFailed(
                                        taskId = syncTask.id,
                                        executionId = executionId,
                                        syncObject,
                                        message = getString(operationName, errorMsg)
                                    ))
                                }
                            }
                    }
                }
            }
    }


    private fun getString(@StringRes stringRes: Int): String = resources.getString(stringRes)

    private fun getString(@StringRes stringRes: Int, vararg arguments: Any) = resources.getString(stringRes, arguments)


    companion object {
        val TAG: String = SyncTaskDirsCreator::class.java.simpleName
    }
}


val List<SyncObject>.names: String get() = joinToString(", ") { it.name }


@AssistedFactory
interface SyncTaskDirsCreatorAssistedFactory {
    fun create(syncObjectLogger: SyncObjectLogger): SyncTaskDirsCreator
}