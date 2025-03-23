package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class OnlyInSourceInstructionGenerator @AssistedInject constructor(
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
                generateSyncInstructionsFrom(it, SyncOperation6.DELETE_IN_TARGET, nextOrderNum)
            }
    }

    private suspend fun deleteDirsInSourceDeletedInTarget(nextOrderNum: Int): Int {
        return getOnlyInSourceComparisonStates()
            .filter { it.isDir }
            .filter { it.isDeletedInTarget }
            .let {
                generateSyncInstructionsFrom(it, SyncOperation6.DELETE_IN_TARGET, nextOrderNum)
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
                    it,
                    SyncOperation6.COPY_FROM_SOURCE_TO_TARGET,
                    nextOrderNum
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
                    it,
                    SyncOperation6.COPY_FROM_SOURCE_TO_TARGET,
                    nextOrderNum
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