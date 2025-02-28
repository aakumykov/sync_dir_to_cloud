package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.extensions.classNameWithHash
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.UUID
import javax.inject.Inject

class SyncInstructionCreator @AssistedInject constructor(
    @Assisted private val initialOrderNum: Int,
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
) {
    private var orderNum = initialOrderNum

    /*fun createInstructionForSourceAndTargetCommonItems(
        commonObject: SyncObject,
        objectInSource: SyncObject,
        objectInTarget: SyncObject,
        syncMode: SyncMode
    ): SyncInstruction5 {
        val sourceAction = InstructionMatrix.getFor(syncMode, objectInSource)
        val targetAction = InstructionMatrix.getFor(syncMode, objectInTarget)
        return SyncInstruction5(
            id = UUID.randomUUID().toString(),
            orderNum = orderNum++,
            taskId = syncTask.id,
            executionId = executionId,

        )
    }*/


    /*fun createInstructionForSourceOnlyItem(
        syncObject: SyncObject,
        syncMode: SyncMode
    ): SyncInstruction5 {
        return when(syncObject.stateInStorage) {
            StateInStorage.NEW ->
            StateInStorage.MODIFIED ->
            StateInStorage.DELETED ->
            StateInStorage.UNCHANGED  ->
        }
    }*/


    /*fun createInstructionForTargetOnlyItem(
        syncObject: SyncObject,
        syncMode: SyncMode
    ): SyncInstruction5 {
        return when(syncMode) {
            SyncMode.SYNC_LOCAL ->
                SyncMode.SYNC_REMOTE ->
                SyncMode.UPDATE_LOCAL ->
                SyncMode.UPDATE_REMOTE ->
                SyncMode.MIRROR_SYNC ->
                SyncMode.MIRROR_UPDATE ->
        }
    }*/


}


@AssistedFactory
interface SyncInstructionCreatorAssistedFactory {
    fun create(initialOrderNum: Int,
               syncTask: SyncTask,
               executionId: String): SyncInstructionCreator
}