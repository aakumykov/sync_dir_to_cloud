package com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_backuper

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.operation_logger.OperationLogger
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.BackupDirSpec
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
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

class DirBackuper @AssistedInject constructor(
    @Assisted private val syncStuff: SyncStuff,
    private val syncObjectReader: SyncObjectReader,
    private val coroutineScope: CoroutineScope,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
) {
    private val operationLogger: OperationLogger get() = syncStuff.operationLogger

    // TODO: убрать @Throws
    @Throws(NullPointerException::class)
    suspend fun backupDeletedDirs(syncTask: SyncTask) {

        val operationName = R.string.SYNC_OPERATION_backuping_deleted_dir

        coroutineScope.launch (coroutineDispatcher) {

            syncObjectReader.getAllObjectsForTask(syncTask.id)
                .filter { it.isDir }
                .filter { it.isDeleted }
                .forEach { syncObject ->
                    try {
                        operationLogger.logOperationStarts(syncObject, operationName)
                         createDir(syncStuff.backupDirSpec, syncObject.name)
                        operationLogger.logOperationSuccess(syncObject, operationName)
                    }
                    catch (e: Exception) {
                        operationLogger.logOperationError(syncObject, operationName, e)
                        Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
                    }
                }
        }
    }

    private fun createDir(backupDirSpec: BackupDirSpec, dirName: String) {
        syncStuff.cloudWriter.createDir(
            File(backupDirSpec.parentDirPath, backupDirSpec.backupDirName).absolutePath,
            dirName
        )
    }

    companion object {
        val TAG: String = DirBackuper::class.java.simpleName
    }
}

