package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.BackupDirSpec
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuffHolder
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeleted
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogUpdater
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.jvm.Throws

class DirBackuper @Inject constructor(
    private val syncStuffHolder: SyncStuffHolder,
    private val syncObjectReader: SyncObjectReader,
    private val coroutineScope: CoroutineScope,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
) {
    private var _syncStuff: SyncStuff? = null
    private val syncStuff get() = _syncStuff!!

    private val syncObjectLogger: SyncObjectLogger get() = syncStuff.syncObjectLogger


    @Throws(NullPointerException::class)
    suspend fun backupDeletedDirs(syncTask: SyncTask) {

        _syncStuff = syncStuffHolder.get(syncTask.id)!!

        coroutineScope.launch (coroutineDispatcher) {

            syncObjectReader.getAllObjectsForTask(syncTask.id)
                .filter { it.isDir }
                .filter { it.isDeleted }
                .let { it }
                .forEach { syncObject ->
                    try {
                        logOperationStarts(syncTask.id, syncObject)
                         createDir(syncStuff.backupDirSpec, syncObject.name)
                        logOperationSuccess(syncTask.id, syncObject)
                    }
                    catch (e: Exception) {
                        logOperationError(syncTask.id, syncObject, e)
                    }
                }
        }
    }

    private suspend fun logOperationStarts(taskId: String, syncObject: SyncObject) {
        syncObjectLogger.apply {
            logWaiting(syncObject = syncObject,
                operationName = R.string.SYNC_OPERATION_create_backup_dir
            )
        }
    }

    private suspend fun logOperationSuccess(taskId: String, syncObject: SyncObject) {
        syncObjectLogger.apply {
            logSuccess(syncObject = syncObject,
                operationName = R.string.SYNC_OPERATION_create_backup_dir
            )
        }
    }

    private suspend fun logOperationError(id: String, syncObject: SyncObject, e: Exception) {
        syncObjectLogger.apply {
            logError(
                syncObject = syncObject,
                operationName = R.string.SYNC_OPERATION_create_backup_dir,
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