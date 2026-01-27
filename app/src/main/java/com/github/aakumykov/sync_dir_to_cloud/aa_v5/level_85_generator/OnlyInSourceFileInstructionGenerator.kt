package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_85_generator

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.notDeletedInSource
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
class OnlyInSourceFileInstructionGenerator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    comparisonStateRepository: ComparisonStateRepository,
    syncInstructionRepository: SyncInstructionRepository,
) : BasicFileInstructionGenerator (
        taskId = syncTask.id,
        executionId = executionId,
        comparisonStateRepository = comparisonStateRepository,
        syncInstructionRepository = syncInstructionRepository
    )
{
    suspend fun generateFileInstructionsForSync(initialOrderNum: Int): Int {
        Log.d(TAG, "generateForSync() called with: initialOrderNum = $initialOrderNum")

        var nextOrderNum = initialOrderNum

        nextOrderNum = createDirsFromSourceInTarget(nextOrderNum)
        nextOrderNum = copyFilesFromSourceToTarget(nextOrderNum)

        return nextOrderNum
    }


    suspend fun generateFileInstructionsForMirror(initialOrderNum: Int): Int {
        Log.d(TAG, "generateForMirror() called with: initialOrderNum = $initialOrderNum")

        var nextOrderNum = initialOrderNum

        nextOrderNum = deleteFilesInSourceDeletedInTarget(nextOrderNum)
        nextOrderNum = deleteDirsInSourceDeletedInTarget(nextOrderNum)

        nextOrderNum = generateFileInstructionsForSync(nextOrderNum)

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

    companion object {
        val TAG: String = OnlyInSourceFileInstructionGenerator::class.java.simpleName
    }
}



@AssistedFactory
interface OnlyInSourceInstructionGeneratorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): OnlyInSourceFileInstructionGenerator
}