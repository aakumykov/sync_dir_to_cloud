package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.backuper.FileAndDirBackuperAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.ItemCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.ItemCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.ItemDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.ItemDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectBackuper5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectBackuperAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectCollisionResolverAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_x_logger.SyncOperationLogger
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_x_logger.SyncOperationLoggerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncInstructionUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class SyncInstructionExecutor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    @Assisted private val scope: CoroutineScope,

    private val syncOptions: SyncOptions,
    private val syncObjectReader: SyncObjectReader,

    private val itemCopierAssistedFactory: ItemCopierAssistedFactory5,
    private val itemDeleterAssistedFactory5: ItemDeleterAssistedFactory5,
    private val collisionResolverAssistedFactory: SyncObjectCollisionResolverAssistedFactory,

    private val syncInstructionUpdater: SyncInstructionUpdater,
    private val syncOperationLoggerAssistedFactory: SyncOperationLoggerAssistedFactory,

    private val fileAndDirBackuperAssistedFactory: FileAndDirBackuperAssistedFactory,
    private val syncObjectBackuperAssistedFactory: SyncObjectBackuperAssistedFactory,
){
    suspend fun execute(instruction: SyncInstruction) {

        val operation = instruction.operation

        when(operation) {
            SyncOperation.RESOLVE_COLLISION -> resolveCollisionFor(instruction)

            SyncOperation.COPY_FROM_SOURCE_TO_TARGET -> copyFromSourceToTarget(instruction)
            SyncOperation.COPY_FROM_TARGET_TO_SOURCE -> copyFromTargetToSource(instruction)

            SyncOperation.DELETE_IN_SOURCE -> deleteInSource(instruction)
            SyncOperation.DELETE_IN_TARGET -> deleteInTarget(instruction)

            SyncOperation.BACKUP_IN_SOURCE_WITH_COPY -> { backupItem(instruction, SyncSide.SOURCE) }
            SyncOperation.BACKUP_IN_SOURCE_WITH_MOVE -> { backupItem(instruction, SyncSide.SOURCE) }
            SyncOperation.BACKUP_IN_TARGET_WITH_COPY -> { backupItem(instruction, SyncSide.TARGET) }
            SyncOperation.BACKUP_IN_TARGET_WITH_MOVE -> { backupItem(instruction, SyncSide.TARGET) }

            SyncOperation.DO_NOTHING_IN_SOURCE -> {}
            SyncOperation.DO_NOTHING_IN_TARGET -> {}
        }

        // Спорно делать это здесь, а не в каждом конкретном методе...
        syncInstructionUpdater.markAsProcessed(instruction.id)
    }

    private suspend fun backupItem(syncInstruction: SyncInstruction, syncSide: SyncSide) {
        when(val operation = syncInstruction.operation) {
            SyncOperation.BACKUP_IN_SOURCE_WITH_COPY -> { backupWithCopy(syncInstruction, syncSide) }
            SyncOperation.BACKUP_IN_SOURCE_WITH_MOVE -> { backupWithMove(syncInstruction, syncSide) }
            SyncOperation.BACKUP_IN_TARGET_WITH_COPY -> { backupWithCopy(syncInstruction, syncSide) }
            SyncOperation.BACKUP_IN_TARGET_WITH_MOVE -> { backupWithMove(syncInstruction, syncSide) }
            else -> throw IllegalArgumentException("Argument must contains a kind of 'BACKUP' operation, now it is '${operation}'")
        }
    }

    private suspend fun backupWithCopy(syncInstruction: SyncInstruction, syncSide: SyncSide) {
        when(syncSide) {
            SyncSide.SOURCE -> backupWithCopyInSource(syncInstruction)
            SyncSide.TARGET -> backupWithCopyInTarget(syncInstruction)
        }
    }

    private suspend fun backupWithCopyInSource(syncInstruction: SyncInstruction) {

        val syncObject = syncObjectReader.getSyncObject(syncInstruction.objectIdInSource!!)
        val absolutePath = syncObject!!.absolutePathIn(syncTask.sourcePath!!)

        sourceBackuper.apply {
            if (syncInstruction.isDir) backupDir(absolutePath)
            else backupFileByCopy(absolutePath)
        }
    }

    private suspend fun backupWithCopyInTarget(syncInstruction: SyncInstruction) {

        val syncObject = syncObjectReader.getSyncObject(syncInstruction.objectIdInTarget!!)
        val absolutePath = syncObject!!.absolutePathIn(syncTask.targetPath!!)

        targetBackuper.apply {
            if (syncInstruction.isDir) backupDir(absolutePath)
            else backupFileByCopy(absolutePath)
        }
    }

    private suspend fun backupWithMove(syncInstruction: SyncInstruction, syncSide: SyncSide) {
        when(syncSide) {
            SyncSide.SOURCE -> backupWithMoveInSource(syncInstruction)
            SyncSide.TARGET -> backupWithMoveInTarget(syncInstruction)
        }
    }

    private suspend fun backupWithMoveInSource(syncInstruction: SyncInstruction) {

        val syncObject = syncObjectReader.getSyncObject(syncInstruction.objectIdInSource!!)
        val absolutePath = syncObject!!.absolutePathIn(syncTask.sourcePath!!)

        sourceBackuper.apply {
            if (syncInstruction.isDir) backupDir(absolutePath)
            else backupFileByMove(absolutePath, SyncSide.SOURCE)
        }
    }

    private suspend fun backupWithMoveInTarget(syncInstruction: SyncInstruction) {

        val syncObject = syncObjectReader.getSyncObject(syncInstruction.objectIdInTarget!!)
        val absolutePath = syncObject!!.absolutePathIn(syncTask.targetPath!!)

        targetBackuper.apply {
            if (syncInstruction.isDir) backupDir(absolutePath)
            else backupFileByMove(absolutePath, SyncSide.TARGET)
        }
    }

    private suspend fun resolveCollisionFor(syncInstruction: SyncInstruction) {
        syncOperationLogger.logWaiting(syncInstruction).also { logItemId ->
            try {
                collisionResolver.resolveCollision(syncInstruction.objectIdInSource!!, syncInstruction.objectIdInTarget!!)
                syncOperationLogger.logSuccess(logItemId)
            } catch (e: Exception) {
                syncOperationLogger.logFail(logItemId, e.errorMsg)
                logE(e)
            }
        }
    }

    private suspend fun copyFromSourceToTarget(syncInstruction: SyncInstruction) {
        syncOperationLogger.logWaiting(syncInstruction).also { logItemId ->
            try {
                val sourceObjectId = syncInstruction.objectIdInSource!!
                syncObjectReader.getSyncObject(sourceObjectId)?.also {
                    itemCopier.copyItemFromSourceToTarget(it, syncOptions.overwriteIfExists)
                } ?: {
                    throw NoSourceObjectInDatabase(sourceObjectId)
                }
                syncOperationLogger.logSuccess(logItemId)
            } catch (e: Exception) {
                syncOperationLogger.logFail(logItemId, e.errorMsg)
                logE(e)
            }
        }
    }

    private suspend fun copyFromTargetToSource(syncInstruction: SyncInstruction) {
        syncOperationLogger.logWaiting(syncInstruction).also { logItemId ->
            try {
                val targetObjectId = syncInstruction.objectIdInTarget!!
                syncObjectReader.getSyncObject(targetObjectId)?.also {
                    itemCopier.copyItemFromTargetToSource(it, syncOptions.overwriteIfExists)
                } ?: run {
                    throw NoTargetObjectInDatabase(targetObjectId)
                }
                syncOperationLogger.logSuccess(logItemId)
            } catch (e: Exception) {
                syncOperationLogger.logFail(logItemId, e.errorMsg)
                logE(e)
            }
        }
    }


    /**
     * // FIXME: удалять сначала файлы, потом каталоги...
     * // Это делается в [SyncInstructionsProcessor.processInstructions]
     */
    private suspend fun deleteInSource(syncInstruction: SyncInstruction) {
        syncOperationLogger.logWaiting(syncInstruction).also { logItemId ->
            val sourceItemId = syncInstruction.objectIdInSource!!
            try {
                syncObjectReader.getSyncObject(sourceItemId)?.also {
                    itemDeleter.deleteItemInSource(it)
                } ?: {
                    throw NoSourceObjectInDatabase(sourceItemId)
                }
                syncOperationLogger.logSuccess(logItemId)
            } catch (e: Exception) {
                syncOperationLogger.logFail(logItemId, e.errorMsg)
                logE(e)
            }
        }
    }

    private suspend fun deleteInTarget(syncInstruction: SyncInstruction) {
        syncOperationLogger.logWaiting(syncInstruction).also { logItemId ->
            val targetItemId = syncInstruction.objectIdInTarget!!
            try {
                syncObjectReader.getSyncObject(targetItemId)?.also {
                    itemDeleter.deleteItemInTarget(it)
                } ?: {
                    throw NoTargetObjectInDatabase(targetItemId)
                }
                syncOperationLogger.logSuccess(logItemId)
            } catch (e: Exception) {
                syncOperationLogger.logFail(logItemId, e.errorMsg)
                logE(e)
            }
        }
    }


    private fun logNoSourceObjectInDatabase(sourceObjectId: String) {
        logE("Source object with id='$sourceObjectId' not found in database!")
    }

    private fun logNoTargetObjectInDatabase(targetObjectId: String) {
        logE("Target object with id='$targetObjectId' not found in database!")
    }


    private val itemCopier: ItemCopier5 by lazy {
        itemCopierAssistedFactory.create(syncTask, executionId)
    }

    private val backuper: SyncObjectBackuper5 by lazy {
        syncObjectBackuperAssistedFactory.create(syncTask, executionId)
    }

    private val itemDeleter: ItemDeleter5 by lazy {
        itemDeleterAssistedFactory5.create(syncTask, executionId)
    }

    private val collisionResolver by lazy {
        collisionResolverAssistedFactory.create(syncTask)
    }

    private val syncOperationLogger: SyncOperationLogger by lazy {
        syncOperationLoggerAssistedFactory.create(syncTask.id, executionId)
    }


    private fun logE(e: Exception) {
        Log.e(TAG, e.errorMsg, e)
    }

    private fun logE(errorMsg: String) {
        Log.e(TAG, errorMsg)
    }

    private val sourceBackuper by lazy {
        fileAndDirBackuperAssistedFactory.create(syncTask, SyncSide.SOURCE)
    }

    private val targetBackuper by lazy {
        fileAndDirBackuperAssistedFactory.create(syncTask, SyncSide.TARGET)
    }

    companion object {
        val TAG: String = SyncInstructionExecutor::class.java.simpleName
    }
}


@AssistedFactory
interface SyncInstructionExecutorAssistedFactory {
    fun create(
        syncTask: SyncTask,
        executionId: String,
        scope: CoroutineScope): SyncInstructionExecutor
}


class NoSourceObjectInDatabase(errorMsg: String) : RuntimeException(errorMsg)
class NoTargetObjectInDatabase(errorMsg: String) : RuntimeException(errorMsg)