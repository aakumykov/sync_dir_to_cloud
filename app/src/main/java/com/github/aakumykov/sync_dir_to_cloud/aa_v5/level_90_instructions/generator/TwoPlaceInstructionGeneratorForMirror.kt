package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.generator

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isNewOrModifiedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isNewOrModifiedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isUnchangedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isUnchangedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isUnchangedOrDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isUnchangedOrDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.PartsLabel
import com.github.aakumykov.sync_dir_to_cloud.repository.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TwoPlaceInstructionGeneratorForMirror @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository: SyncInstructionRepository,
) {
    suspend fun generate(initialOrderNum: Int): Int {
        var nextOrderNum = initialOrderNum

        nextOrderNum = processWhatToDelete(nextOrderNum)
        nextOrderNum = processWhatToRename(nextOrderNum)
        nextOrderNum = processWhatToCopy(nextOrderNum)

        return nextOrderNum
    }


    private suspend fun processWhatToBackup(isDir: Boolean, nextOrderNum: Int): Int {
        return createSyncInstructionsFrom(
            getStatesForDeletionInTarget(isDir),
            SyncOperation.BACKUP_IN_TARGET_WITH_MOVE,
            nextOrderNum
        )
    }


    private suspend fun processWhatToDelete(nextOrderNum: Int): Int {
        var n = nextOrderNum
        // Сначала каталоги, потом файлы! Чтобы не путаться,
        // хорошо бы эту логику инкапсулировать.
        n = deleteObjectsFromTargetDeletedInSourceAndUnchangedInTarget(isDir = false, nextOrderNum = n)
        n = deleteObjectsFromSourceDeletedInTargetAndUnchangedInSource(isDir = false, nextOrderNum = n)

        n = deleteObjectsFromTargetDeletedInSourceAndUnchangedInTarget(isDir = true, nextOrderNum = n)
        n = deleteObjectsFromSourceDeletedInTargetAndUnchangedInSource(isDir = true, nextOrderNum = n)
        return n
    }

    private suspend fun deleteObjectsFromTargetDeletedInSourceAndUnchangedInTarget(isDir: Boolean, nextOrderNum: Int): Int {
        return createSyncInstructionsFrom(
            getStatesForDeletionInTarget(isDir),
            deleteOrBackupInTarget(),
            nextOrderNum
        )
    }

    private suspend fun deleteObjectsFromSourceDeletedInTargetAndUnchangedInSource(isDir: Boolean, nextOrderNum: Int): Int {
        return createSyncInstructionsFrom(
            getStatesForDeletionInSource(isDir),
            deleteOrBackupInSource(),
            nextOrderNum
        )
    }


    private fun deleteOrBackupInTarget(): SyncOperation {
        return if (syncTask.withBackup) SyncOperation.BACKUP_IN_TARGET_WITH_MOVE
        else SyncOperation.DELETE_IN_TARGET
    }

    private fun deleteOrBackupInSource(): SyncOperation {
        return if (syncTask.withBackup) SyncOperation.BACKUP_IN_SOURCE_WITH_MOVE
        else SyncOperation.DELETE_IN_SOURCE
    }


    private suspend fun getStatesForDeletionInSource(isDir: Boolean): List<ComparisonState> {
        return getBilateralStatesOfObjectType(isDir)
            .filter { it.isDeletedInTarget }
            .filter { it.isUnchangedInSource }
    }

    private suspend fun getStatesForDeletionInTarget(isDir: Boolean): List<ComparisonState> {
        return getBilateralStatesOfObjectType(isDir)
            .filter { it.isDeletedInSource }
            .filter { it.isUnchangedInTarget }
    }

    private suspend fun getBilateralStatesOfObjectType(isDir: Boolean): List<ComparisonState> {
        return getAllBilateralComparisonStates()
            .filter { if (isDir) it.isDir else it.isFile }
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
                    it,
                    listOf(
                        SyncOperation.RESOLVE_COLLISION,
                        SyncOperation.COPY_FROM_SOURCE_TO_TARGET,
                        SyncOperation.COPY_FROM_TARGET_TO_SOURCE
                    ),
                    nextOrderNum
                )
            }
    }


    private suspend fun processWhatToCopy(nextOrderNum: Int): Int {
        var n = nextOrderNum
        n = copyFilesFromTargetToSourceNewOrModifiedInTargetAndUnchangedOrDeletedInSource(n)
        n = copyFilesFromSourceToTargetNewOrModifiedInSourceAndUnchangedOrDeletedInTarget(n)
        return n
    }


    private suspend fun copyFilesFromTargetToSourceNewOrModifiedInTargetAndUnchangedOrDeletedInSource(nextOrderNum: Int): Int {

        var non = nextOrderNum

        return getAllBilateralComparisonStates()
            .filter { it.isFile }
            .filter { it.isNewOrModifiedInTarget }
            .filter { it.isUnchangedOrDeletedInSource }
            .forEach { comparisonState ->

                SyncInstruction.from(
                    partsLabel = PartsLabel.ST,
                    comparisonState = comparisonState,
                    operation = doNothingOrBackupInSource(comparisonState),
                    orderNum = non++
                )

                SyncInstruction.from(
                    partsLabel = PartsLabel.ST,
                    comparisonState = comparisonState,
                    operation = SyncOperation.COPY_FROM_TARGET_TO_SOURCE,
                    orderNum = non++
                )
            }
            .let {
                non
            }
    }


    private fun doNothingOrBackupInSource(comparisonState: ComparisonState): SyncOperation {
        return if (comparisonState.isUnchangedInSource) SyncOperation.BACKUP_IN_SOURCE_WITH_COPY
        else SyncOperation.DO_NOTHING_IN_SOURCE
    }


    private fun doNothingOrBackupInTarget(comparisonState: ComparisonState): SyncOperation {
        return if (comparisonState.isUnchangedInTarget) SyncOperation.BACKUP_IN_TARGET_WITH_COPY
        else SyncOperation.DO_NOTHING_IN_TARGET
    }


    private suspend fun copyFilesFromSourceToTargetNewOrModifiedInSourceAndUnchangedOrDeletedInTarget(nextOrderNum: Int): Int {

        var non = nextOrderNum

        return getAllBilateralComparisonStates()
            .filter { it.isFile }
            .filter { it.isNewOrModifiedInSource }
            .filter { it.isUnchangedOrDeletedInTarget }
            .forEach { comparisonState ->

                SyncInstruction.from(
                    partsLabel = PartsLabel.ST,
                    comparisonState = comparisonState,
                    operation = doNothingOrBackupInTarget(comparisonState),
                    orderNum = non++
                )

                SyncInstruction.from(
                    partsLabel = PartsLabel.ST,
                    comparisonState = comparisonState,
                    operation = SyncOperation.COPY_FROM_SOURCE_TO_TARGET,
                    orderNum = non++
                )
            }
            .let {
                non
            }
    }



    private suspend fun createSyncInstructionsFrom(
        list: List<ComparisonState>,
        syncOperationList: List<SyncOperation>,
        nextOrderNum: Int
    ): Int {
        var n = nextOrderNum
        list.forEach { comparisonState ->
            syncInstructionRepository.apply {
                syncOperationList.forEach { syncOperation ->
                    add(SyncInstruction.from(
                        partsLabel = PartsLabel.ST,
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
        syncOperation: SyncOperation,
        nextOrderNum: Int
    ): Int {
        return createSyncInstructionsFrom(
            list,
            listOf(syncOperation),
            nextOrderNum
        )
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