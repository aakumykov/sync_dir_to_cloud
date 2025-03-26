package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notMutuallyUnchanged
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * У файлов, находящихся только в приёмнике, два пути:
 * 1) в режиме SYNC они игнорируются;
 * 2) в режиме MIRROR копируются в источник.
 */
class OnlyInTargetInstructionGenerator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository: SyncInstructionRepository6,
)
    : BasicInstructionGenerator(
        taskId = syncTask.id,
        executionId = executionId,
        comparisonStateRepository = comparisonStateRepository,
        syncInstructionRepository = syncInstructionRepository
    )
{
    /**
     * @return Порядковый номер для следующего генератора инструкций.
     */
    suspend fun generateForSync(initialOrderNum: Int): Int {
        var nextOrderNum = initialOrderNum
        return nextOrderNum
    }

    /**
     * @return Порядковый номер для следующего генератора инструкций.
     */
    suspend fun generateForMirror(initialOrderNum: Int): Int {
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
                generateSyncInstructionsFrom(it, SyncOperation6.DELETE_IN_SOURCE, nextOrderNum)
            }
    }

    private suspend fun processDirsNeedToBeDeletedInSource(nextOrderNum: Int): Int {
        return getOnlyInTargetStates()
            .filter { it.isDir }
            .filter { it.isDeletedInTarget }
            .let {
                generateSyncInstructionsFrom(it, SyncOperation6.DELETE_IN_SOURCE, nextOrderNum)
            }
    }


    private suspend fun processDirsNeedToBeCreatedInSource(nextOrderNum: Int): Int {
        return getOnlyInTargetStates()
            .filter { it.isDir }
            .filter { it.notMutuallyUnchanged }
            .filter { it.notDeletedInTarget }
            .let {
                generateSyncInstructionsFrom(it, SyncOperation6.COPY_FROM_TARGET_TO_SOURCE, nextOrderNum)
            }
    }


    private suspend fun processFilesNeedToBeCopiedToSource(nextOrderNum: Int): Int {
        return getOnlyInTargetStates()
            .filter { it.isFile }
            .filter { it.notMutuallyUnchanged }
            .filter { it.notDeletedInTarget }
            .let {
                generateSyncInstructionsFrom(it, SyncOperation6.COPY_FROM_TARGET_TO_SOURCE, nextOrderNum)
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
}


@AssistedFactory
interface OnlyInTargetInstructionGeneratorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): OnlyInTargetInstructionGenerator
}