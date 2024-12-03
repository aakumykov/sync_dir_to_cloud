package com.github.aakumykov.sync_dir_to_cloud.aa_v3.file_copier

import android.util.Log
import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.kotlin_playground.counting_buffered_streams.CountingBufferedInputStream
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_creator.DirCreator.Companion.TAG
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.operation_logger.OperationLogger
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.actualSize
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNew
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class FileCopier @AssistedInject constructor(
    @Assisted private val syncStuff: SyncStuff,
    @Assisted private val coroutineScope: CoroutineScope,
    @Assisted private val executionId: String,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectStateChanger: SyncObjectStateChanger,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
    private val operationCancellationHolder: OperationCancellationHolder,
) {
    private val operationLogger: OperationLogger get() = syncStuff.operationLogger
    private val cloudReader: CloudReader get() = syncStuff.cloudReader
    private val cloudWriter: CloudWriter get() = syncStuff.cloudWriter


    suspend fun copyNewFiles(syncTask: SyncTask) {

        val operationName = R.string.SYNC_OPERATION_copy_new_file

        operationCancellationHolder.addJob("copy_new_file",
            coroutineScope.launch (coroutineDispatcher) {
                try {
                    copyNewFilesReal(syncTask, operationName)
                }
                catch (e: CancellationException) {
                    Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
                }
            }
        )
    }


    private suspend fun copyNewFilesReal(syncTask: SyncTask, operationName: Int) {

        syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { it.isFile }
            .filter { it.isNew }
            // TODO: фильтровать те, которые сейчас не в работе...
            .forEach { syncObject ->
                copyOneFile(syncObject, syncTask, operationName)
            }
    }


    private suspend fun copyOneFile(syncObject: SyncObject, syncTask: SyncTask, operationName: Int) {

        try {
            operationLogger.logOperationStarts(syncObject, operationName)
            syncObjectStateChanger.markAsBusy(syncObject.id)

            val inputStream = cloudReader
                .getFileInputStream(syncObject.absolutePathIn(syncTask.sourcePath!!))
                .getOrThrow()

            val progressCalculator = ProgressCalculator(syncObject.actualSize)

            var lastProgressValue: Float? = 0.0f

            val countingInputStream = CountingBufferedInputStream(inputStream) { readedCount ->

                val progress = progressCalculator.calcProgress(readedCount)

                // Реализация пропуска повторяющихся значений
                if (lastProgressValue != progress) {
                    lastProgressValue = progress

                    coroutineScope.launch (coroutineDispatcher) {
                        operationLogger.logProgress(
                            syncObject.id,
                            syncTask.id,
                            executionId,
                            progressCalculator.progressAsPartOf100(progress)
                        )
                    }
                }
            }

            cloudWriter.putStream(
                countingInputStream,
                syncObject.absolutePathIn(syncTask.targetPath!!),
                true
            )

            syncObjectStateChanger.markAsSuccessfullySynced(syncObject.id)
            operationLogger.logOperationSuccess(syncObject, operationName)
        }
        catch (e: Exception) {
            Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
            syncObjectStateChanger.markAsError(syncObject.id, ExceptionUtils.getErrorMessage(e))
            operationLogger.logOperationError(syncObject, operationName, e)
        }
    }

}