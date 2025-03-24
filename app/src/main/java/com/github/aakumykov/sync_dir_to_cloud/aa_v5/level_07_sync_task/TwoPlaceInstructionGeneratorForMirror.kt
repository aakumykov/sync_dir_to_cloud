package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isNewOrModifiedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isNewOrModifiedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceDeletedAndTargetModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceDeletedAndTargetNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceDeletedAndTargetUnchanged
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceModifiedAndTargetDeleted
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceModifiedAndTargetModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceModifiedAndTargetNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceModifiedAndTargetUnchanged
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceNewAndTargetDeleted
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceNewAndTargetModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceNewAndTargetNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceNewAndTargetUnchanged
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceUnchangedTargetDeleted
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceUnchangedTargetModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceUnchangedTargetNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isUnchangedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isUnchangedOrDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isUnchangedOrDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TwoPlaceInstructionGeneratorForMirror @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository6: SyncInstructionRepository6,
) {
    suspend fun generate(initialOrderNum: Int): Int {
        var nextOrderNum = initialOrderNum

        nextOrderNum = processWhatToDelete(nextOrderNum)
        nextOrderNum = processWhatToRename(nextOrderNum)
        nextOrderNum = processWhatToCopy(nextOrderNum)

        return nextOrderNum
    }


    private suspend fun processWhatToDelete(nextOrderNum: Int): Int {
        var n = nextOrderNum
        n = deleteObjectsFromTargetDeletedInSourceAndUnchangedInTarget(isDir = true, nextOrderNum = n)
        n = deleteObjectsFromTargetDeletedInSourceAndUnchangedInTarget(isDir = false, nextOrderNum = n)
        return n
    }

    private suspend fun deleteObjectsFromTargetDeletedInSourceAndUnchangedInTarget(isDir: Boolean, nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { if (isDir) it.isDir else it.isFile }
            .filter { it.isDeletedInSource }
            .filter { it.isUnchangedInTarget }
            .let { createSyncInstructionsFrom(it, SyncOperation6.DELETE_IN_TARGET, nextOrderNum) }
    }


    private suspend fun processWhatToRename(nextOrderNum: Int): Int {
        var n = nextOrderNum
        n = renameObjectsMutuallyNewOrChanged(isDir = true, n)
        n = renameObjectsMutuallyNewOrChanged(isDir = false, n)
        return n
    }


    private suspend fun renameObjectsMutuallyNewOrChanged(isDir: Boolean, nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { if (isDir) it.isDir else it.isFile }
            .filter { it.isNewOrModifiedInSource }
            .filter { it.isNewOrModifiedInTarget }
            .let {
                createSyncInstructionsFrom(
                    list = it,
                    listOf(
                        SyncOperation6.RENAME_COLLISION_IN_SOURCE,
                        SyncOperation6.RENAME_COLLISION_IN_TARGET
                    ),
                    nextOrderNum
                )
            }
    }


    private suspend fun processWhatToCopy(nextOrderNum: Int): Int {
        var n = nextOrderNum
        n = copyFilesToSourceNewOrModifiedInTargetAndUnchangedOrDeletedInSource(n)
        n = copyFilesToTargetNewOrModofiedInSourceAndUnchangedOrDeletedInTarget(n)
        return n
    }


    private suspend fun copyFilesToSourceNewOrModifiedInTargetAndUnchangedOrDeletedInSource(nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { it.isFile }
            .filter { it.isNewOrModifiedInTarget }
            .filter { it.isUnchangedOrDeletedInSource }
            .let { createSyncInstructionsFrom(it, SyncOperation6.COPY_FROM_SOURCE_TO_TARGET, nextOrderNum) }
    }


    private suspend fun copyFilesToTargetNewOrModofiedInSourceAndUnchangedOrDeletedInTarget(nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { it.isFile }
            .filter { it.isNewOrModifiedInSource }
            .filter { it.isUnchangedOrDeletedInTarget }
            .let { createSyncInstructionsFrom(it, SyncOperation6.COPY_FROM_TARGET_TO_SOURCE, nextOrderNum) }
    }



    private suspend fun createSyncInstructionsFrom(
        list: List<ComparisonState>,
        syncOperationList: List<SyncOperation6>,
        nextOrderNum: Int
    ): Int {
        var n = nextOrderNum
        list.forEach { comparisonState ->
            syncInstructionRepository6.apply {
                syncOperationList.forEach { syncOperation ->
                    add(SyncInstruction6.from(
                        comparisonState = comparisonState,
                        operation = syncOperation,
                        orderNum = n++
                    ))
                }
            }
        }
        return n
    }

    private suspend fun createSyncInstructionsFrom(
        list: List<ComparisonState>,
        syncOperation: SyncOperation6,
        nextOrderNum: Int
    ): Int {
        return createSyncInstructionsFrom(list, listOf(syncOperation), nextOrderNum)
    }

    // Нужно использовать именно такой метод, каждый раз получая
    // свежий список из репозитория. Потому что в случае переименований он меняется.
    // FIXME: хотя, это чревато несогласованностью
    private suspend fun getAllBilateralComparisonStates(): Iterable<ComparisonState>
        = comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter { it.isBilateral }

    companion object {
        val TAG: String = TwoPlaceInstructionGeneratorForMirror::class.java.simpleName
    }
}


@AssistedFactory
interface TwoPlaceInstructionGeneratorForMirrorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TwoPlaceInstructionGeneratorForMirror
}