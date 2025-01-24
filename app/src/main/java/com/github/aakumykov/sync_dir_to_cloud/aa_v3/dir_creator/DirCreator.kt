package com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_creator

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.operation_logger.OperationLogger
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNew
import com.github.aakumykov.sync_dir_to_cloud.extensions.basePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DirCreator @AssistedInject constructor(
    @Assisted private val syncStuff: SyncStuff,
    @Assisted private val coroutineScope: CoroutineScope,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
    private val operationCancellationHolder: OperationCancellationHolder,
) {
    private val operationLogger: OperationLogger get() = syncStuff.operationLogger
    private val cloudWriter: CloudWriter get() = syncStuff.cloudWriter

    fun createNewDirs(syncTask: SyncTask) {

        val operationName = R.string.SYNC_OPERATION_creating_new_dir

        operationCancellationHolder.addJob("qwerty",
            coroutineScope.launch (coroutineDispatcher) {
                try {
                    createDirs(syncTask, operationName)
                }
                catch (e: CancellationException) {
                    Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
                }
            }
        )
    }

    private suspend fun createDirs(syncTask: SyncTask, operationName: Int) {

        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isDir }
            .filter { it.isNew }
            .forEach { syncObject ->
                createOneDir(syncObject, syncTask, operationName)
            }
    }

    private suspend fun createOneDir(syncObject: SyncObject, syncTask: SyncTask, operationName: Int) {
        try {
            operationLogger.logOperationStarts(syncObject, operationName)
            syncObjectStateChanger.markAsBusy(syncObject.id)

            /*repeat(5) { i ->
                Log.d(TAG, "Ожидание создания каталога «${syncObject.name}» ...$i")
                delay(1000)
            }*/

            cloudWriter.createDir(
                syncObject.basePathIn(syncTask.targetPath!!),
                syncObject.name)

            syncObjectStateChanger.markAsSuccessfullySynced(syncObject.id)
            operationLogger.logOperationSuccess(syncObject, operationName)
        }
        catch (e: Exception) {
            Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
            syncObjectStateChanger.markAsError(syncObject.id, e)
            operationLogger.logOperationError(syncObject, operationName, e)
        }
    }

    companion object {
        val TAG: String = DirCreator::class.java.simpleName
    }
}