package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.files_backuper

import android.util.Log
import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.BackupDirCreatorCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target.dirs.isDeleted
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
            .also { list -> processList(list, syncTask) }
    }

    // TODO: регистрировать ошибку
    private suspend fun processList(list: List<SyncObject>, syncTask: SyncTask) {

        if (list.isEmpty()) {
            Log.d(TAG, "Бэкап файлов для задачи ${syncTask.description} не требуется.")
            return
        }

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
            try {

                val sourceFilePath = syncObject.absolutePathIn(syncTask.targetPath!!)
                val backupFilePath = syncObject.absolutePathIn(backupDirPath)

                cloudReader.getFileInputStream(sourceFilePath)
                    .getOrThrow()
                    .also { inputStream ->
                        cloudWriter.putFile(inputStream, backupFilePath)
                    }

            } catch (e: Exception) {
                ExceptionUtils.getErrorMessage(e).also { errorMsg ->
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
