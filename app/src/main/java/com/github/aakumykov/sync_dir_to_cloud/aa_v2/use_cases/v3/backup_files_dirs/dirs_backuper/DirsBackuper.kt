package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.dirs_backuper

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.BackupDirCreatorCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs.names
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeleted
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isTargetReadingOk
import com.github.aakumykov.sync_dir_to_cloud.extensions.relativePath
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_object_logger.SyncObjectLogger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DirsBackuper @AssistedInject constructor(
    @Assisted private val cloudWriter: CloudWriter,
    @Assisted private val executionId: String,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val backupDirCreatorCreator: BackupDirCreatorCreator,
    private val syncObjectLogger: SyncObjectLogger,
    private val resources: Resources,
){
    suspend fun backupDeletedDirsOfTask(syncTask: SyncTask) {
        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isDir }
            .filter { it.isDeleted }
            .filter { it.isTargetReadingOk } // Можно обрабатывать только те элементы, состояние которых в приёмнике известно.
            .also { list ->
                if (list.isNotEmpty()) Log.d(TAG + "_" + SyncTaskExecutor.TAG, "backupDeletedDirsOfTask(${list.names})")
                processList(list, syncTask)
            }
    }


    private suspend fun processList(list: List<SyncObject>, syncTask: SyncTask) {

        if (list.isEmpty()) {
//            Log.d(FilesBackuper.TAG, "Бэкап каталогов для задачи не требуется ${syncTask.description}")
            return
        }

//        list.joinToString(", ") { it.name }.also { Log.d(com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.files_backuper.FilesBackuper.TAG, "list: $it") }

        backupDirCreatorCreator.createBackupDirCreatorFor(syncTask)
            ?.createBackupDirFor(syncTask)
            ?.onSuccess { backupDirPath ->
                processListReal(list, syncTask, backupDirPath)
            }
            ?.onFailure {
                Log.e(TAG, "Ошибка создания каталога для бэкапа для задачи ${syncTask.description}")
            }
    }


    private suspend fun processListReal(list: List<SyncObject>, syncTask: SyncTask, backupDirPath: String) {
        list.forEach { syncObject ->

            val objectId = syncObject.id

            try {
                syncObjectStateChanger.setBackupState(objectId, ExecutionState.RUNNING)

                cloudWriter
                    .createDirResult(backupDirPath, syncObject.relativePath)
                    .getOrThrow()

                syncObjectStateChanger.setBackupState(objectId, ExecutionState.SUCCESS)
                syncObjectLogger.log(SyncObjectLogItem.createSuccess(
                    syncTask = syncTask,
                    executionId = executionId,
                    syncObject = syncObject,
                    operationName = getString(R.string.SYNC_OBJECT_LOGGER_backuping_dir)
                ))
            }
            catch (e: Exception) {
                ExceptionUtils.getErrorMessage(e).also { errorMsg ->
                    syncObjectStateChanger.setBackupState(objectId, ExecutionState.ERROR, errorMsg)
                    Log.e(TAG, errorMsg, e)
                    syncObjectLogger.log(SyncObjectLogItem.createFailed(
                        syncTask = syncTask,
                        executionId = executionId,
                        syncObject = syncObject,
                        operationName = getString(R.string.SYNC_OBJECT_LOGGER_backuping_dir),
                        errorMessage = errorMsg
                    ))
                }
            }
        }
    }


    private fun getString(@StringRes stringRes: Int): String = resources.getString(stringRes)

    private fun getString(@StringRes stringRes: Int, vararg arguments: Any) = resources.getString(stringRes, arguments)


    companion object {
        val TAG: String = DirsBackuper::class.java.simpleName
    }
}


@AssistedFactory
interface DirsBackuperAssistedFactory {
    fun create(cloudWriter: CloudWriter, executionId: String): DirsBackuper
}
