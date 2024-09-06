package com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_deleter

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.operation_logger.OperationLogger
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeleted
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DirDeleter @AssistedInject constructor(
    @Assisted private val syncStuff: SyncStuff,
    private val syncObjectReader: SyncObjectReader,
    private val coroutineScope: CoroutineScope,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
) {
    private val operationLogger: OperationLogger get() = syncStuff.operationLogger
    private val cloudWriter: CloudWriter get() = syncStuff.cloudWriter


    fun deleteDeletedDirs(syncTask: SyncTask) {

        coroutineScope.launch (coroutineDispatcher) {
            try {
                deleteDirs(syncTask)
            }
            catch (e: CancellationException) {
                Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
            }
        }
    }

    private suspend fun deleteDirs(syncTask: SyncTask) {

        val operationName = R.string.SYNC_OPERATION_deleting_deleted_dir

        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isDir }
            .filter { it.isDeleted }
            .forEach { syncObject ->
                try {
                    operationLogger.logOperationStarts(syncObject, operationName)

                    repeat(5) { i ->
                        Log.d(TAG, "задержка $i...")
                        delay(1000)
                    }

                    cloudWriter.deleteFile(
                        syncObject.absolutePathIn(syncTask.sourcePath!!),
                        syncObject.name)

                    operationLogger.logOperationSuccess(syncObject, operationName)
                }
                catch(e: Exception) {
                    operationLogger.logOperationError(syncObject, operationName, e)
                }
            }
    }

    companion object {
        val TAG: String = DirDeleter::class.java.simpleName
    }
}