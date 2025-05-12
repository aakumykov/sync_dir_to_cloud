package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.generator

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.PartsLabel
import com.github.aakumykov.sync_dir_to_cloud.repository.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * У файлов, находящихся только в источнике, один путь:
 * копируются в приёмник (да?)
 */
class OnlyInSourceInstructionGenerator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    comparisonStateRepository: ComparisonStateRepository,
    syncInstructionRepository: SyncInstructionRepository,
)
    : BasicInstructionGenerator(
        taskId = syncTask.id,
        executionId = executionId,
        comparisonStateRepository = comparisonStateRepository,
        syncInstructionRepository = syncInstructionRepository
    )
{
    suspend fun generateForSync(initialOrderNum: Int): Int {
        var nextOrderNum = initialOrderNum

        nextOrderNum = createDirsFromSourceInTarget(nextOrderNum)
        nextOrderNum = copyFilesFromSourceToTarget(nextOrderNum)

        return nextOrderNum
    }


    suspend fun generateForMirror(initialOrderNum: Int): Int {
        var nextOrderNum = initialOrderNum

        nextOrderNum = deleteFilesInSourceDeletedInTarget(nextOrderNum)
        nextOrderNum = deleteDirsInSourceDeletedInTarget(nextOrderNum)

        nextOrderNum = generateForSync(nextOrderNum)

        return nextOrderNum
    }


    private suspend fun deleteFilesInSourceDeletedInTarget(nextOrderNum: Int): Int {
        return getOnlyInSourceComparisonStates()
            .filter { it.isFile }
            .filter { it.isDeletedInTarget }
            .let {
                generateSyncInstructionsFrom(
                    partsLabel = PartsLabel.S,
                    comparisonStateList = it,
                    syncOperation = SyncOperation.DELETE_IN_TARGET,
                    nextOrderNum = nextOrderNum
                )
            }
    }

    private suspend fun deleteDirsInSourceDeletedInTarget(nextOrderNum: Int): Int {
        return getOnlyInSourceComparisonStates()
            .filter { it.isDir }
            .filter { it.isDeletedInTarget }
            .let {
                generateSyncInstructionsFrom(
                    partsLabel = PartsLabel.S,
                    comparisonStateList = it,
                    syncOperation = SyncOperation.DELETE_IN_TARGET,
                    nextOrderNum = nextOrderNum
                )
            }
    }

    private suspend fun createDirsFromSourceInTarget(nextOrderNum: Int): Int {
        return getOnlyInSourceComparisonStates()
            .filter { it.isDir }
            .let { it }
            .filter { it.notDeletedInSource }
            .let { it }
            .let {
                generateSyncInstructionsFrom(
                    partsLabel = PartsLabel.S,
                    comparisonStateList = it,
                    syncOperation = SyncOperation.COPY_FROM_SOURCE_TO_TARGET,
                    nextOrderNum = nextOrderNum,
                )
            }
    }


    private suspend fun copyFilesFromSourceToTarget(nextOrderNum: Int): Int {
        return getOnlyInSourceComparisonStates()
            .filter { it.isFile }
            .let { it }
            .filter { it.notDeletedInSource }
            .let { it }
            .let {
                generateSyncInstructionsFrom(
                    partsLabel = PartsLabel.S,
                    comparisonStateList = it,
                    syncOperation = SyncOperation.COPY_FROM_SOURCE_TO_TARGET,
                    nextOrderNum = nextOrderNum
                )
            }
    }


    private suspend fun getOnlyInSourceComparisonStates(): Iterable<ComparisonState> {
        return getStatesForThisTaskAndExecution()
            .filter { it.onlySource }
    }
}



@AssistedFactory
interface OnlyInSourceInstructionGeneratorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): OnlyInSourceInstructionGenerator
}