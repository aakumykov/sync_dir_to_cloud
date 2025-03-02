package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.UUID

class SyncInstructionCreator @AssistedInject constructor(
    @Assisted private val initialOrderNum: Int,
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    @Assisted private val withBackup: Boolean,
) {
    /*fun create(
        syncMode: SyncMode,
        sourceObject: SyncObject,
        targetObject: SyncObject
    ): SyncInstruction5? {
        val sourceObjectState = sourceObject.stateInStorage.name
        val targetObjectState = targetObject.stateInStorage.name
        val combinedState = sourceObjectState+"_"+targetObjectState
        return when(syncMode) {
            SyncMode.SYNC_REMOTE ->  createForSyncRemote(combinedState, sourceObject, targetObject)
            else -> null
        }
    }*/

    /*private fun createForSyncRemote(
        combinedState: String,
        sourceObject: SyncObject,
        targetObject: SyncObject
    ): SyncInstruction5? {
        return when(combinedState) {
            StateInStorage.UNCHANGED_UNCHANGED -> null
            StateInStorage.NEW_NEW ->
        }
    }*/


    /*fun create(
        syncMode: SyncMode,
        sourceObject: SyncObject?,
        targetObject: SyncObject?,
    ): SyncInstruction5 {
        return when(syncMode) {
            SyncMode.SYNC_LOCAL -> makeSyncOneSideInstruction(sourceObject = targetObject, targetObject = sourceObject)
            SyncMode.SYNC_REMOTE -> makeSyncOneSideInstruction(sourceObject = sourceObject, targetObject = targetObject)
//            SyncMode.UPDATE_LOCAL -> makeUpdateOneSideInstruction(mode = syncMode, from = targetObject, to = sourceObject)
//            SyncMode.UPDATE_REMOTE -> makeUpdateOneSideInstruction(mode = syncMode, from = sourceObject, to = targetObject)
//            SyncMode.MIRROR_SYNC -> makeMirrorSyncInstruction(sourceObject = sourceObject, targetObject = targetObject)
//            SyncMode.MIRROR_UPDATE -> makeMirrorUpdateInstruction(sourceObject = sourceObject, targetObject = targetObject)
        }
    }*/

    /*private fun makeSyncOneSideInstruction(
        sourceObject: SyncObject?,
        targetObject: SyncObject?,
    ): SyncInstruction5 {
        return when {
            (null != sourceObject && null == targetObject) -> SyncInstruction5(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                executionId = executionId,
                executionOrderNum = nextOrderNum,
                groupOrderNum = 1,
                sourceObjectId = sourceObject.id,
                targetObjectId = null,
                operation = SyncOperation.copyVariant(withBackup),
                name = sourceObject.name,
            )
            (null == sourceObject && null != targetObject) -> SyncInstruction5(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                executionId = executionId,
                executionOrderNum = nextOrderNum,
                groupOrderNum = 1,
                sourceObjectId = null,
                targetObjectId = targetObject.id,
                operation = SyncOperation.copyVariant(withBackup),
                name = targetObject.name
            )
            else -> throw IllegalArgumentException("Both arguments cannot be null!")
        }
    }*/

    private var orderNum = initialOrderNum
    private val nextOrderNum: Int get() {
        orderNum++
        return orderNum
    }

    /*return SyncInstruction5(
            id = UUID.randomUUID().toString(),
            taskId = taskId,
            executionId = executionId,
            executionOrderNum = orderNum++,
            groupOrderNum =
        )*/
}


@AssistedFactory
interface SyncInstructionCreatorAssistedFactory {
    fun create(initialOrderNum: Int,
               @Assisted(QUALIFIER_TASK_ID) taskId: String,
               @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
               withBackup: Boolean): SyncInstructionCreator
}