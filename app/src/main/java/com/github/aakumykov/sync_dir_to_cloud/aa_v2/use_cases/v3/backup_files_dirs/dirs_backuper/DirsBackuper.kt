package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.dirs_backuper

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.BackupDirCreatorCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.files_backuper.FilesBackuper
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target.dirs.isDeleted
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.relativePath
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DirsBackuper @AssistedInject constructor(
    @Assisted private val cloudWriter: CloudWriter,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    private val backupDirCreatorCreator: BackupDirCreatorCreator,
){
    suspend fun backupDeletedDirsOfTask(syncTask: SyncTask) {
        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isDir }
            .filter { it.isDeleted }
            .filter { it.targetReadingStateIsOk } // Можно обрабатывать только те элементы, состояние которых в приёмнике известно.
            .also { list -> processList(list, syncTask) }
    }


    private suspend fun processList(list: List<SyncObject>, syncTask: SyncTask) {

        if (list.isEmpty()) {
            Log.d(FilesBackuper.TAG, "Бэкап каталогов для задачи не требуется ${syncTask.description}")
            return
        }

        list.joinToString(", ") { it.name }.also { Log.d(com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.files_backuper.FilesBackuper.TAG, "list: $it") }

        backupDirCreatorCreator.createBackupDirCreatorFor(syncTask)
            ?.createBackupDirFor(syncTask)
            ?.onSuccess { backupDirPath ->
                processListReal(list, syncTask, backupDirPath)
            }
            ?.onFailure {
                Log.d(TAG, "Ошибка создания каталога для бэкапа для задачи ${syncTask.description}")
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
            }
            catch (e: Exception) {
                ExceptionUtils.getErrorMessage(e).also { errorMsg ->
                    syncObjectStateChanger.setBackupState(objectId, ExecutionState.ERROR, errorMsg)
                    Log.e(TAG, errorMsg, e)
                }
            }
        }
    }


    companion object {
        val TAG: String = DirsBackuper::class.java.simpleName
    }
}


@AssistedFactory
interface DirsBackuperAssistedFactory {
    fun create(cloudWriter: CloudWriter): DirsBackuper
}


val SyncObject.targetReadingStateIsOk: Boolean get() = ExecutionState.SUCCESS == targetReadingState