package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StoragePriority
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class InstructionCreator5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val bilateralInstructionCreator: BilateralInstructionCreator,
//    private val
) {
    fun createSyncInstruction(
        comparisonState: ComparisonState,
        syncMode: SyncMode,
        storagePriority: StoragePriority,
        withBackup: Boolean,
        onlyAdd: Boolean,
    ): SyncInstruction5 {
        return when(syncMode) {
            SyncMode.SYNC -> createInstructionForSync(
                comparisonState,
                storagePriority,
                withBackup,
                onlyAdd
            )
            SyncMode.MIRROR -> createInstructionForMirror(
                comparisonState,
                storagePriority,
                withBackup,
                onlyAdd
            )
        }
    }

    private fun createInstructionForSync(
        comparisonState: ComparisonState,
        storagePriority: StoragePriority,
        withBackup: Boolean,
        onlyAdd: Boolean
    ): SyncInstruction5 {
        return when(val combinedState = StateInStorage.combine(comparisonState)) {
            StateInStorage.UNCHANGED_null -> unchangedNull(combinedState, storagePriority, withBackup, onlyAdd)
            StateInStorage.UNCHANGED_UNCHANGED -> unchangedUnchanged(combinedState, storagePriority, withBackup, onlyAdd)
            StateInStorage.UNCHANGED_NEW -> unchangedNew(comparisonState, storagePriority, withBackup, onlyAdd)
            StateInStorage.UNCHANGED_MODIFIED -> unchangedModified(comparisonState, storagePriority, withBackup, onlyAdd)
            StateInStorage.UNCHANGED_DELETED -> unchangedDeleted(comparisonState, storagePriority, withBackup, onlyAdd)
            else -> throw IllegalArgumentException("Bad combined state '${combinedState}'")
        }
    }

    private fun unchangedDeleted(
        comparisonState: ComparisonState,
        storagePriority: StoragePriority,
        withBackup: Boolean,
        onlyAdd: Boolean
    ): SyncInstruction5 {
        TODO("Not yet implemented")
    }

    private fun unchangedModified(
        comparisonState: ComparisonState,
        storagePriority: StoragePriority,
        withBackup: Boolean,
        onlyAdd: Boolean
    ): SyncInstruction5 {
        TODO("Not yet implemented")
    }

    private fun unchangedNew(
        comparisonState: ComparisonState,
        storagePriority: StoragePriority,
        withBackup: Boolean,
        onlyAdd: Boolean
    ): SyncInstruction5 {
        TODO("Not yet implemented")
    }

    private fun unchangedNull(
        combinedState: String,
        storagePriority: StoragePriority,
        withBackup: Boolean,
        onlyAdd: Boolean
    ): SyncInstruction5 {
        TODO("Not yet implemented")
    }

    private fun unchangedUnchanged(
        combinedState: String,
        storagePriority: StoragePriority,
        withBackup: Boolean,
        onlyAdd: Boolean
    ): SyncInstruction5 {
        TODO("Not yet implemented")
    }

    /*private val ACTION_FOR_UNCHANGED: (StateInStorage) -> Unit = { otherItemState ->
        when (otherItemState) {
            StateInStorage.UNCHANGED -> unchangedUnchanged()
            StateInStorage.NEW -> SyncAction.
            StateInStorage.MODIFIED ->
            StateInStorage.DELETED ->
        }
    }

    private val ACTION_FOR_NEW: (StateInStorage) -> Unit = { otherItemState ->

    }

    private val ACTION_FOR_MODIFIED: (StateInStorage) -> Unit = { otherItemState ->

    }

    private val ACTION_FOR_DELETED: (StateInStorage) -> Unit = { otherItemState ->

    }

    private val BILITERAL_MATRIX = mapOf(
        StateInStorage.UNCHANGED to ACTION_FOR_UNCHANGED,
        StateInStorage.NEW to ACTION_FOR_NEW,
        StateInStorage.MODIFIED to ACTION_FOR_MODIFIED,
        StateInStorage.DELETED to ACTION_FOR_DELETED
    )*/

    private fun createInstructionForMirror(
        comparisonState: ComparisonState,
        storagePriority: StoragePriority,
        withBackup: Boolean,
        onlyAdd: Boolean
    ): SyncInstruction5 {
        throw RuntimeException()
    }

    companion object {
        val TAG: String = InstructionCreator5::class.java.simpleName
    }
}


@AssistedFactory
interface InstructionCreatorAssistedFactory5 {
    fun create(syncTask: SyncTask, executionId: String): InstructionCreator5
}


class BilateralInstructionCreator @Inject constructor() {
    fun create(
        comparisonState: ComparisonState,
        storagePriority: StoragePriority,
        withBackup: Boolean,
        onlyAdd: Boolean
    ): SyncInstruction5 {
        return when(comparisonState.sourceObjectState!!) {
            StateInStorage.UNCHANGED -> unchangedAnd(comparisonState.targetObjectState!!)
            StateInStorage.NEW -> newAnd(comparisonState.targetObjectState!!)
            StateInStorage.MODIFIED -> modifiedAnd(comparisonState.targetObjectState!!)
            StateInStorage.DELETED -> deletedAnd(comparisonState.targetObjectState!!)
        }
    }

    private fun unchangedAnd(targetObjectState: StateInStorage): SyncInstruction5 {
        TODO("Not yet implemented")
    }

    private fun newAnd(targetObjectState: StateInStorage): SyncInstruction5 {
        TODO("Not yet implemented")
    }

    private fun modifiedAnd(targetObjectState: StateInStorage): SyncInstruction5 {
        TODO("Not yet implemented")
    }

    private fun deletedAnd(targetObjectState: StateInStorage): SyncInstruction5 {
        TODO("Not yet implemented")
    }
}