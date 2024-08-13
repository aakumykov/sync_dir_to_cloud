package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.files_backuper

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.BackupDirCreatorCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs.names
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeleted
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isModified
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isTargetReadingOk
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_object_logger.SyncObjectLogger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

// TODO: где устанавливать статус SyncTask-а "выполняется РК": здесь или в SyncTaskExecutor?
/**
 * cloudReader и cloudWriter оба должны быть созданы на основе типа приёмника.
 */
class FilesBackuper @AssistedInject constructor(
    @Assisted private val cloudReader: CloudReader,
    @Assisted private val cloudWriter: CloudWriter,
    @Assisted private val executionId: String,
    private val syncObjectReader: SyncObjectReader,
    private val backupDirCreatorCreator: BackupDirCreatorCreator,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val syncObjectLogger: SyncObjectLogger,
    private val resources: Resources,
) {

    suspend fun backupDeletedFilesOfTask(syncTask: SyncTask) {
        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isFile }
            .filter { it.isDeleted }
            .filter { it.isTargetReadingOk } // Можно обрабатывать только те элементы, состояние которых в приёмнике известно.
            .also { list ->
                if (list.isNotEmpty()) Log.d(TAG + "_" + SyncTaskExecutor.TAG, "backupDeletedFilesOfTask(${list.names})")
                processDeletedFilesList(list, syncTask)
            }
    }

    suspend fun backupModifiedFilesOfTask(syncTask: SyncTask) {
        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isFile }
            .filter { it.isModified }
            .filter { it.isTargetReadingOk }
            .also { list ->
                if (list.isNotEmpty()) Log.d(TAG + "_" + SyncTaskExecutor.TAG, "backupModifiedFilesOfTask(${list.names})")
                processModifiedFilesList(list, syncTask)
            }
    }


    private suspend fun processDeletedFilesList(list: List<SyncObject>, syncTask: SyncTask) {
        if (list.isEmpty()) {
//            Log.d(TAG+"_"+SyncTaskExecutor.TAG, "Бэкап удалённых файлов для задачи не требуется ${syncTask.description}")
            return
        }
        processList(list, syncTask)
    }

    private suspend fun processModifiedFilesList(list: List<SyncObject>, syncTask: SyncTask) {
        if (list.isEmpty()) {
//            Log.d(TAG+"_"+SyncTaskExecutor.TAG, "Бэкап изменившихся файлов для задачи не требуется ${syncTask.description}")
            return
        }
        processList(list, syncTask)
    }


    // TODO: регистрировать ошибку (где?)
    private suspend fun processList(list: List<SyncObject>, syncTask: SyncTask) {

//        list.joinToString(", ") { it.name }.also { Log.d(TAG+"_"+SyncTaskExecutor.TAG, "list: $it") }

        backupDirCreatorCreator.createBackupDirCreatorFor(syncTask)?.also { backupDirCreator ->

            backupDirCreator.createBackupDirFor(syncTask)
                .onSuccess { backupDirPath ->
                    processFiles(list, backupDirPath, syncTask)
                }
                .onFailure {
                    ExceptionUtils.getErrorMessage(it).also { errorMsg ->
                        Log.e(TAG, errorMsg, it)
                    }
                }
        }
    }

    private suspend fun processFiles(list: List<SyncObject>, backupDirPath: String, syncTask: SyncTask) {

        list.forEach { syncObject ->

            val objectId = syncObject.id

            try {

                syncObjectStateChanger.setBackupState(objectId, ExecutionState.RUNNING)

                val sourceFilePath = syncObject.absolutePathIn(syncTask.targetPath!!)
                val backupFilePath = syncObject.absolutePathIn(backupDirPath)

                cloudReader.getFileInputStream(sourceFilePath)
                    .getOrThrow()
                    .also { inputStream ->
                        cloudWriter.putFile(inputStream, backupFilePath)
                    }

                syncObjectStateChanger.setBackupState(objectId, ExecutionState.SUCCESS)

                syncObjectLogger.log(SyncObjectLogItem.createSuccess(
                    taskId = syncObject.taskId,
                    executionId = executionId,
                    syncObject = syncObject,
                    operationName = getString(R.string.SYNC_OBJECT_LOGGER_backuping_file)
                ))

            } catch (e: Exception) {
                ExceptionUtils.getErrorMessage(e).also { errorMsg ->
                    syncObjectStateChanger.setBackupState(objectId, ExecutionState.ERROR, errorMsg)
                    Log.e(TAG, errorMsg, e)
                    syncObjectLogger.log(SyncObjectLogItem.createFailed(
                        taskId = syncObject.taskId,
                        executionId = executionId,
                        syncObject = syncObject,
                        operationName = getString(R.string.SYNC_OBJECT_LOGGER_backuping_file),
                        errorMessage = errorMsg
                    ))
                }
            }
        }
    }


    private fun getString(@StringRes stringRes: Int): String = resources.getString(stringRes)

    private fun getString(@StringRes stringRes: Int, vararg arguments: Any) = resources.getString(stringRes, arguments)


    companion object {
        val TAG: String = FilesBackuper::class.java.simpleName
    }
}

@AssistedFactory
interface FilesBackuperAssistedFactory {
    fun createFilesBackuper(
        cloudReader: CloudReader,
        cloudWriter: CloudWriter,
        executionId: String
    ): FilesBackuper
}
