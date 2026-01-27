package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_85_generator

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.notDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.notMutuallyUnchanged
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.PartsLabel
import com.github.aakumykov.sync_dir_to_cloud.repository.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * У файлов, находящихся только в приёмнике, такие пути:
 * 1) в режиме SYNC они игнорируются;
 * 2) в режиме MIRROR копируются в источник или удаляются из источника.
 */
class OnlyInTargetFileInstructionGenerator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository: SyncInstructionRepository,
) : BasicFileInstructionGenerator(
        taskId = syncTask.id,
        executionId = executionId,
        comparisonStateRepository = comparisonStateRepository,
        syncInstructionRepository = syncInstructionRepository
    )
{
    /**
     * @return Порядковый номер для следующего генератора инструкций.
     */
    suspend fun generateFileInstructionsForSync(initialOrderNum: Int): Int {
        Log.d(TAG, "generateForSync() called with: initialOrderNum = $initialOrderNum")
        val nextOrderNum = initialOrderNum
        return nextOrderNum
    }

    /**
     * @return Порядковый номер для следующего генератора инструкций.
     */
    suspend fun generateFileInstructionsForMirror(initialOrderNum: Int): Int {
        Log.d(TAG, "generateForMirror() called with: initialOrderNum = $initialOrderNum")

        var nextOrderNum = initialOrderNum

        nextOrderNum = processFilesNeedToBeDeletedInSource(nextOrderNum)
        nextOrderNum = processDirsNeedToBeDeletedInSource(nextOrderNum)

        nextOrderNum = processDirsNeedToBeCreatedInSource(nextOrderNum)
        nextOrderNum = processFilesNeedToBeCopiedToSource(nextOrderNum)

        return nextOrderNum
    }


    private suspend fun processFilesNeedToBeDeletedInSource(nextOrderNum: Int): Int {
        return getOnlyInTargetStates()
            .filter { it.isFile }
            .filter { it.isDeletedInTarget }
            .let {
                generateSyncInstructionsFrom(
                    partsLabel = PartsLabel.T,
                    comparisonStateList = it,
                    syncOperation = SyncOperation.DELETE_IN_SOURCE,
                    nextOrderNum = nextOrderNum
                )
            }
    }

    private suspend fun processDirsNeedToBeDeletedInSource(nextOrderNum: Int): Int {
        return getOnlyInTargetStates()
            .filter { it.isDir }
            .filter { it.isDeletedInTarget }
            .let {
                generateSyncInstructionsFrom(
                    partsLabel = PartsLabel.T,
                    comparisonStateList = it,
                    syncOperation = SyncOperation.DELETE_IN_SOURCE,
                    nextOrderNum = nextOrderNum
                )
            }
    }


    private suspend fun processDirsNeedToBeCreatedInSource(nextOrderNum: Int): Int {
        return getOnlyInTargetStates()
            .filter { it.isDir }
            .filter { it.notMutuallyUnchanged }
            .filter { it.notDeletedInTarget }
            .let {
                generateSyncInstructionsFrom(
                    partsLabel = PartsLabel.T,
                    comparisonStateList = it,
                    syncOperation = SyncOperation.COPY_FROM_TARGET_TO_SOURCE,
                    nextOrderNum = nextOrderNum,
                )
            }
    }


    private suspend fun processFilesNeedToBeCopiedToSource(nextOrderNum: Int): Int {
        return getOnlyInTargetStates()
            .filter { it.isFile }
            .filter { it.notMutuallyUnchanged }
            .filter { it.notDeletedInTarget }
            .let {
                generateSyncInstructionsFrom(
                    partsLabel = PartsLabel.T,
                    comparisonStateList = it,
                    syncOperation = SyncOperation.COPY_FROM_TARGET_TO_SOURCE,
                    nextOrderNum = nextOrderNum
                )
            }
    }

    private suspend fun getOnlyInTargetStates(): Iterable<ComparisonState> {
        return getStatesForThisTaskAndExecution()
            .filter { null == it.sourceObjectState }
    }

    /*private suspend fun processDirsNeedsToBeDeleted(nextOrderNum: Int): Int {
        return getStates()
            .filter { it.onlyTarget }
            .filter { it.isDir }
            .filter { it.isDeletedInSource }
            .let {
               generateSyncInstructionsFrom(it, SyncOperation6.DELETE_IN_SOURCE, nextOrderNum)
            }
    }


    private suspend fun processFilesNeedsToBeDeleted(nextOrderNum: Int): Int {
        return getStates()
            .filter { it.onlyTarget }
            .filter { it.isFile }
            .filter { it.notUnchangedOrDeletedInTarget }
            .let {
                generateSyncInstructionsFrom(it, SyncOperation6.DELETE_IN_SOURCE, nextOrderNum)
            }
    }*/

    companion object {
        val TAG: String = OnlyInTargetFileInstructionGenerator::class.java.simpleName
    }
}


@AssistedFactory
interface OnlyInTargetInstructionGeneratorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): OnlyInTargetFileInstructionGenerator
}