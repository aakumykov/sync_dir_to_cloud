package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.files_backuper

import android.util.Log
import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.BackupDirCreatorCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.dirs_backuper.targetReadingStateIsOk
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target.dirs.isDeleted
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
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
    private val syncObjectReader: SyncObjectReader,
    private val backupDirCreatorCreator: BackupDirCreatorCreator,
    private val syncObjectStateChanger: SyncObjectStateChanger,
) {

    suspend fun backupDeletedFilesOfTask(syncTask: SyncTask) {
        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isFile }
            .filter { it.isDeleted }
            .filter { it.targetReadingStateIsOk } // Можно обрабатывать только те элементы, состояние которых в приёмнике известно.
            .also { list -> processDeletedFilesList(list, syncTask) }
    }

    suspend fun backupModifiedFilesOfTask(syncTask: SyncTask) {
        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isFile }
            .filter { it.isModified }
            .filter { it.targetReadingStateIsOk }
            .also { list -> processModifiedFilesList(list, syncTask) }
    }


    private suspend fun processDeletedFilesList(list: List<SyncObject>, syncTask: SyncTask) {

        if (list.isEmpty()) {
            Log.d(TAG, "Бэкап удалённых файлов для задачи не требуется ${syncTask.description}")
            return
        }

        processList(list, syncTask)
    }

    private suspend fun processModifiedFilesList(list: List<SyncObject>, syncTask: SyncTask) {
        if (list.isEmpty()) {
            Log.d(TAG, "Бэкап изменившихся файлов для задачи не требуется ${syncTask.description}")
            return
        }
        processList(list, syncTask)
    }


    // TODO: регистрировать ошибку (где?)
    private suspend fun processList(list: List<SyncObject>, syncTask: SyncTask) {

        list.joinToString(", ") { it.name }.also { Log.d(TAG, "list: $it") }

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

            } catch (e: Exception) {
                ExceptionUtils.getErrorMessage(e).also { errorMsg ->
                    syncObjectStateChanger.setBackupState(objectId, ExecutionState.ERROR, errorMsg)
                    Log.e(TAG, errorMsg, e)
                }
            }
        }
    }


    companion object {
        val TAG: String = FilesBackuper::class.java.simpleName
    }
}

@AssistedFactory
interface FilesBackuperAssistedFactory {
    fun createFilesBackuper(cloudReader: CloudReader, cloudWriter: CloudWriter): FilesBackuper
}


val SyncObject.isModified: Boolean get() = (StateInSource.MODIFIED == stateInSource)