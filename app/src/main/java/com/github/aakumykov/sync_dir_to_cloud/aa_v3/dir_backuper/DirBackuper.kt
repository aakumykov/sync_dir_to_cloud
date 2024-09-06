package com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_backuper

import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncObjectLogger
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.BackupDirSpec
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeleted
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import kotlin.jvm.Throws

class DirBackuper @AssistedInject constructor(
    @Assisted private val syncStuff: SyncStuff,
    private val syncObjectReader: SyncObjectReader,
    private val coroutineScope: CoroutineScope,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
) {
    private val syncObjectLogger: SyncObjectLogger get() = syncStuff.syncObjectLogger


    @Throws(NullPointerException::class)
    suspend fun backupDeletedDirs(syncTask: SyncTask) {

        val operationName = R.string.SYNC_OPERATION_create_backup_dir

        coroutineScope.launch (coroutineDispatcher) {

            syncObjectReader.getAllObjectsForTask(syncTask.id)
                .filter { it.isDir }
                .filter { it.isDeleted }
                .forEach { syncObject ->
                    try {
                        logOperationStarts(syncObject, operationName)
                         createDir(syncStuff.backupDirSpec, syncObject.name)
                        logOperationSuccess(syncObject, operationName)
                    }
                    catch (e: Exception) {
                        logOperationError(syncObject, operationName, e)
                    }
                }
        }
    }

    private suspend fun logOperationStarts(
        syncObject: SyncObject,
        operationName: Int
    ) {
        syncObjectLogger.apply {
            logWaiting(syncObject = syncObject, operationName = operationName)
        }
    }

    private suspend fun logOperationSuccess(
        syncObject: SyncObject,
        operationName: Int
    ) {
        syncObjectLogger.apply {
            logSuccess(syncObject = syncObject, operationName = operationName)
        }
    }

    private suspend fun logOperationError(
        syncObject: SyncObject,
        operationName: Int,
        e: Exception
    ) {
        syncObjectLogger.apply {
            logError(
                syncObject = syncObject,
                operationName = operationName,
                errorMsg = ExceptionUtils.getErrorMessage(e)
            )
        }
    }

    private fun createDir(backupDirSpec: BackupDirSpec, dirName: String) {
        syncStuff.cloudWriter.createDir(
            File(backupDirSpec.parentDirPath, backupDirSpec.backupDirName).absolutePath,
            dirName
        )
    }
}

