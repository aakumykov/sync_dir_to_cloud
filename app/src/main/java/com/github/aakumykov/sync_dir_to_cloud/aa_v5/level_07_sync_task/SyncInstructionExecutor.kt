package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.ItemCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.ItemCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.ItemDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.ItemDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectBackuper5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectBackuperAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCollisionResolverAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_x_logger.SyncOperationLogger
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_x_logger.SyncOperationLoggerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
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
    private val backuperAssistedFactory5: SyncObjectBackuperAssistedFactory5,
    private val collisionResolverAssistedFactory: SyncObjectCollisionResolverAssistedFactory,
    private val syncInstructionUpdater: SyncInstructionUpdater,
    private val syncOperationLoggerAssistedFactory: SyncOperationLoggerAssistedFactory,
){
    suspend fun execute(instruction: SyncInstruction6) {

        when(instruction.operation) {
            SyncOperation6.RESOLVE_COLLISION -> resolveCollisionFor(instruction)

            SyncOperation6.COPY_FROM_SOURCE_TO_TARGET -> copyFromSourceToTarget(instruction.objectIdInSource!!)
            SyncOperation6.COPY_FROM_TARGET_TO_SOURCE -> copyFromTargetToSource(instruction.objectIdInTarget!!)

            SyncOperation6.DELETE_IN_SOURCE -> deleteInSource(instruction.objectIdInSource!!)
            SyncOperation6.DELETE_IN_TARGET -> deleteInTarget(instruction.objectIdInTarget!!)

            SyncOperation6.BACKUP_IN_SOURCE -> { /*backuper.backupInSource(instruction)*/ }
            SyncOperation6.BACKUP_IN_TARGET -> { /*backuper.backupInTarget(instruction)*/ }
        }

        // Спорно делать это здесь, а не в каждом конкретном методе...
        syncInstructionUpdater.markAsProcessed(instruction.id)
    }

    private suspend fun resolveCollisionFor(syncInstruction: SyncInstruction6) {
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

    private suspend fun copyFromSourceToTarget(sourceObjectId: String) {
        syncObjectReader.getSyncObject(sourceObjectId)?.also {
            itemCopier.copyItemFromSourceToTarget(it, syncOptions.overwriteIfExists)
        } ?: {
//            TODO: errorLogger.log()
        }
    }

    private suspend fun copyFromTargetToSource(targetObjectId: String) {
        syncObjectReader.getSyncObject(targetObjectId)?.also {
            itemCopier.copyItemFromTargetToSource(it, syncOptions.overwriteIfExists)
        } ?: run {
            // TODO: где и как регистриро БРОСАТЬ ИСКЛЮЧЕНИЕ
        }
    }


    /**
     * // FIXME: удалять сначала файлы, потом каталоги...
     * // Это делается в [SyncInstructionsProcessor6.processInstructions]
     */
    private suspend fun deleteInSource(sourceObjectId: String) {
        syncObjectReader.getSyncObject(sourceObjectId)?.also {
            itemDeleter.deleteItemInSource(it)
        } // TODO: ?: throw Exception
    }

    private suspend fun deleteInTarget(targetObjectId: String) {
        syncObjectReader.getSyncObject(targetObjectId)?.also {
            itemDeleter.deleteItemInTarget(it)
        } // TODO: ?: throw Exception
    }


    private val itemCopier: ItemCopier5 by lazy {
        itemCopierAssistedFactory.create(syncTask, executionId)
    }

    private val backuper: SyncObjectBackuper5 by lazy {
        backuperAssistedFactory5.create(syncTask, executionId)
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