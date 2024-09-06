package com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_deleter

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncObjectLogger
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.operation_logger.OperationLogger
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeleted
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
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

        /*val operationName = R.string.SYNC_OPERATION_deleting_deleted_dir

        coroutineScope.launch (coroutineDispatcher) {

            syncObjectReader.getAllObjectsForTask(syncTask.id)
                .filter { it.isDir }
                .filter { it.isDeleted }
                .forEach { syncObject ->
                    try {

                    }
                    catch(e: Exception) {

                    }
                }
        }*/
    }
}