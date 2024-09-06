package com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_deleter

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncObjectLogger
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_creator.DirCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_creator.DirCreator.Companion
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DirDeleter @AssistedInject constructor(
    @Assisted private val syncStuff: SyncStuff,
    @Assisted private val coroutineScope: CoroutineScope,
    private val syncObjectReader: SyncObjectReader,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
) {
    private val operationLogger: OperationLogger get() = syncStuff.operationLogger
    private val cloudWriter: CloudWriter get() = syncStuff.cloudWriter


    fun deleteDeletedDirs(syncTask: SyncTask) {

        val operationName = R.string.SYNC_OPERATION_deleting_deleted_dir

        coroutineScope.launch (coroutineDispatcher) {

            syncObjectReader.getAllObjectsForTask(syncTask.id)
                .filter { it.isDir }
                .filter { it.isDeleted }
                .forEach { syncObject ->
                    try {
                        operationLogger.logOperationStarts(syncObject, operationName)

                        cloudWriter.deleteFile(
                            syncObject.absolutePathIn(syncTask.sourcePath!!),
                            syncObject.name)

                        operationLogger.logOperationSuccess(syncObject, operationName)
                    }
                    catch(e: Exception) {
                        operationLogger.logOperationError(syncObject, operationName, e)
                        Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
                    }
                }
        }
    }

    companion object {
        val TAG: String = DirDeleter::class.java.simpleName
    }
}