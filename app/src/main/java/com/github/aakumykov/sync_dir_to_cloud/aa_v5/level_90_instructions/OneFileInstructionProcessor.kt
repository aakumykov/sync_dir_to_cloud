package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.FSItemDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.ItemCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.ItemDeleterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectCollisionResolverAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectCopier
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_x_logger.FileOperationLogger
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_x_logger.FileOperationLoggerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch


class OneFileInstructionProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val scope: CoroutineScope,

    private val syncOptions: SyncOptions,

    private val syncObjectDBReader: SyncObjectDBReader,

    private val itemCopierAssistedFactory: ItemCopierAssistedFactory,

    private val itemDeleterAssistedFactory: ItemDeleterAssistedFactory,

    private val collisionResolverAssistedFactory: SyncObjectCollisionResolverAssistedFactory,

    private val syncInstructionUpdater: SyncInstructionUpdater,

    private val fileOperationLoggerAssistedFactory: FileOperationLoggerAssistedFactory,
    private val fileOperationLogger2AssistedFactory: FileOperationLogger2AssistedFactory,

    private val backupInstructionExecutorAssistedFactory: BackupInstructionExecutor2AssistedFactory,

    private val operationCancellationHolder: OperationCancellationHolder,
){
    suspend fun execute(instruction: SyncInstruction) {

        val operation = instruction.operation

        when(operation) {
            SyncOperation.RESOLVE_COLLISION -> resolveCollisionFor(instruction)

            SyncOperation.COPY_FROM_SOURCE_TO_TARGET -> copyFromSourceToTarget(instruction)
            SyncOperation.COPY_FROM_TARGET_TO_SOURCE -> copyFromTargetToSource(instruction)

            SyncOperation.DELETE_IN_SOURCE -> deleteInSource(instruction)
            SyncOperation.DELETE_IN_TARGET -> deleteInTarget(instruction)

            SyncOperation.BACKUP_IN_SOURCE -> { backupInSource(instruction) }
            SyncOperation.BACKUP_IN_TARGET -> { backupInTarget(instruction) }

            SyncOperation.DO_NOTHING_IN_SOURCE -> {}
            SyncOperation.DO_NOTHING_IN_TARGET -> {}
        }

        // Спорно делать это здесь, а не в каждом конкретном методе...
        syncInstructionUpdater.markAsProcessed(instruction.id)
    }

    private suspend fun backupInTarget(instruction: SyncInstruction) {
        /*fileOperationLogger.logWaiting(instruction, null).also { logItemId ->
            try {
                backupInstructionExecutor.backupInTarget(instruction)
                fileOperationLogger.logSuccess(logItemId)
            } catch (e: Exception) {
                fileOperationLogger.logFail(logItemId, e.errorMsg)
            }
        }*/
    }

    private suspend fun backupInSource(instruction: SyncInstruction) {
        /*fileOperationLogger.logWaiting(instruction, null).also { logItemId ->
            try {
                backupInstructionExecutor.backupInSource(instruction)
                fileOperationLogger.logSuccess(logItemId)
            } catch (e: Exception) {
                fileOperationLogger.logFail(logItemId, e.errorMsg)
            }
        }*/
    }


    /**
     * Работа выполняется в [BackupInstructionExecutor2.execute]
     */
    private fun stubBackup() {

    }


    /*private suspend fun backupItem(syncInstruction: SyncInstruction, syncSide: SyncSide) {
        when(val operation = syncInstruction.operation) {
            SyncOperation.BACKUP_IN_SOURCE -> { backupWithCopy(syncInstruction, syncSide) }
            SyncOperation.BACKUP_IN_SOURCE_WITH_MOVE -> { backupWithMove(syncInstruction, syncSide) }
            SyncOperation.BACKUP_IN_TARGET_WITH_COPY -> { backupWithCopy(syncInstruction, syncSide) }
            SyncOperation.BACKUP_IN_TARGET_WITH_MOVE -> { backupWithMove(syncInstruction, syncSide) }
            else -> throw IllegalArgumentException("Argument must contains a kind of 'BACKUP' operation, now it is '${operation}'")
        }
    }*/

    /*private suspend fun backupWithCopy(syncInstruction: SyncInstruction, syncSide: SyncSide) {
        *//*when(syncSide) {
            SyncSide.SOURCE -> syncObjectBackuper.backupWithCopyInSource(syncInstruction)
            SyncSide.TARGET -> syncObjectBackuper.backupWithCopyInTarget(syncInstruction)
        }*//*

        backupInstructionExecutor.backupWithCopy(syncInstruction, syncSide)
    }*/

    /*private suspend fun backupWithMove(syncInstruction: SyncInstruction, syncSide: SyncSide) {
        *//*when(syncSide) {
            SyncSide.SOURCE -> syncObjectBackuper.backupWithMoveInSource(syncInstruction)
            SyncSide.TARGET -> syncObjectBackuper.backupWithMoveInTarget(syncInstruction)
        }*//*

        backupInstructionExecutor.backupWithMove(syncInstruction, syncSide)
    }*/


    private suspend fun resolveCollisionFor(syncInstruction: SyncInstruction) {
        /*fileOperationLogger.logWaiting(syncInstruction, null).also { logItemId ->
            try {
                collisionResolver.resolveCollision(syncInstruction.objectIdInSource!!, syncInstruction.objectIdInTarget!!)
                fileOperationLogger.logSuccess(logItemId)
            } catch (e: Exception) {
                fileOperationLogger.logFail(logItemId, e.errorMsg)
                logE(e)
            }
        }*/
    }


    private suspend fun copyFromSourceToTarget(syncInstruction: SyncInstruction) {

        val logItemId = newRandomId
        val jobCancellationId = newRandomId

        /*val eh = CoroutineExceptionHandler { context, t ->
            scope.launch { fileOperationLogger.logFail(logItemId, t.errorMsg) }
        }*/

        scope.launch (/*eh*/) {

            try {
                val sourceObjectId = syncInstruction.objectIdInSource!!

                fileOperationLogger.logWaiting(logItemId, syncInstruction, jobCancellationId)

                syncObjectDBReader.getSyncObject(sourceObjectId)?.also {

                    itemCopier.copySyncObjectFromSourceToTarget(it, syncOptions.overwriteIfExists)

                } ?: {
                    throw NoSourceObjectInDatabase(sourceObjectId)
                }

                fileOperationLogger.logSuccess(logItemId)

            }
            catch (e: CancellationException) {
                // Здесь корутина переходит в неактивное состояние,
                // поэтому запускать действие приходится в новой области видимости.
                scope.launch {
                    fileOperationLogger.logCancelled(logItemId, e.errorMsg)
                }.join()
            }
            catch (t: Throwable) {
                scope.launch {
                    fileOperationLogger.logFail(logItemId, t.errorMsg)
                }.join()
            }
            finally {
                operationCancellationHolder.removeJob(jobCancellationId)
            }

        }.apply {
            operationCancellationHolder.addJob(jobCancellationId, job)
            join()
        }
    }


    private suspend fun copyFromTargetToSource(syncInstruction: SyncInstruction) {
        /*fileOperationLogger.logWaiting(syncInstruction).also { logItemId ->
            try {
                val targetObjectId = syncInstruction.objectIdInTarget!!
                syncObjectDBReader.getSyncObject(targetObjectId)?.also {
                    itemCopier.copySyncObjectFromTargetToSource(it, syncOptions.overwriteIfExists)
                } ?: run {
                    throw NoTargetObjectInDatabase(targetObjectId)
                }
                fileOperationLogger.logSuccess(logItemId)
            }
            catch (e: StreamToFileCopyingCancellationException) {
                // TODO: обработка
            }
            catch (e: Exception) {
                // FIXME: эта ошибка должна отображаться в интерфейсе!
                fileOperationLogger.logFail(logItemId, e.errorMsg)
                logE(e)
            }
        }*/
    }


    /**
     * // FIXME: удалять сначала файлы, потом каталоги...
     * // Это делается в [FileInstructionsProcessor.processInstructions]
     */
    private suspend fun deleteInSource(syncInstruction: SyncInstruction) {
        /*fileOperationLogger.logWaiting(syncInstruction).also { logItemId ->
            val sourceItemId = syncInstruction.objectIdInSource!!
            try {
                syncObjectDBReader.getSyncObject(sourceItemId)?.also {
                    itemDeleter.deleteItemInSource(it)
                } ?: {
                    throw NoSourceObjectInDatabase(sourceItemId)
                }
                fileOperationLogger.logSuccess(logItemId)
            } catch (e: Exception) {
                fileOperationLogger.logFail(logItemId, e.errorMsg)
                logE(e)
            }
        }*/
    }

    private suspend fun deleteInTarget(syncInstruction: SyncInstruction) {
        /*fileOperationLogger.logWaiting(syncInstruction).also { logItemId ->
            val targetItemId = syncInstruction.objectIdInTarget!!
            try {
                syncObjectDBReader.getSyncObject(targetItemId)?.also {
                    itemDeleter.deleteItemInTarget(it)
                } ?: {
                    throw NoTargetObjectInDatabase(targetItemId)
                }
                fileOperationLogger.logSuccess(logItemId)
            } catch (e: Exception) {
                fileOperationLogger.logFail(logItemId, e.errorMsg)
                logE(e)
            }
        }*/
    }


    private fun logNoSourceObjectInDatabase(sourceObjectId: String) {
        logE("Source object with id='$sourceObjectId' not found in database!")
    }

    private fun logNoTargetObjectInDatabase(targetObjectId: String) {
        logE("Target object with id='$targetObjectId' not found in database!")
    }


    private val itemCopier: SyncObjectCopier by lazy {
        itemCopierAssistedFactory.create(syncTask, executionId, scope)
    }



    private val itemDeleter: FSItemDeleter5 by lazy {
        itemDeleterAssistedFactory.create(syncTask)
    }

    private val collisionResolver by lazy {
        collisionResolverAssistedFactory.create(syncTask)
    }

    private val fileOperationLogger: FileOperationLogger by lazy {
        fileOperationLoggerAssistedFactory.create(syncTask.id, executionId)
    }

    private val fileOperationLogger2: FileOperationLogger2 by lazy {
        fileOperationLogger2AssistedFactory.create(syncTask.id, executionId)
    }


    private fun logE(e: Exception) {
        Log.e(TAG, e.errorMsg, e)
    }

    private fun logE(errorMsg: String) {
        Log.e(TAG, errorMsg)
    }

    private val backupInstructionExecutor: BackupInstructionExecutor2 by lazy {
        backupInstructionExecutorAssistedFactory.create(syncTask)
    }

    companion object {
        val TAG: String = OneFileInstructionProcessor::class.java.simpleName
    }
}


@AssistedFactory
interface OneFileInstructionExecutorAssistedFactory {
    fun create(
        syncTask: SyncTask,
        executionId: String,
        scope: CoroutineScope,
    ): OneFileInstructionProcessor
}


class NoSourceObjectInDatabase(errorMsg: String) : RuntimeException(errorMsg)
class NoTargetObjectInDatabase(errorMsg: String) : RuntimeException(errorMsg)