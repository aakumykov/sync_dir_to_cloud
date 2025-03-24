package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isFile
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isNewModifiedDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isNewOrModifiedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TwoPlaceInstructionGeneratorForSync @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository6: SyncInstructionRepository6,
) {
    /**
     * @return Увеличенный порядковый номер
     */
    suspend fun generate(initialOrderNum: Int): Int {
        var nextOrderNum = initialOrderNum

        // Сначала удаляю, что освобождает место в хранилище (если без бекапа).
        nextOrderNum = processNeedToBeDeletedInTarget(nextOrderNum)

        // Потом копирую новое.
        nextOrderNum = processNeedToBeCopiedToTarget(nextOrderNum)

        return nextOrderNum
    }

    private suspend fun processNeedToBeDeletedInTarget(initialOrderNum: Int): Int {
        var nextOrderNum = initialOrderNum
        // Сначала удаляю все файлы
        nextOrderNum = deleteInTargetFilesDeletedInSource(nextOrderNum)
        // Потом каталоги (которые к этой поре должны стать пустыми).
        // Ибо удаление непустого каталога в облаке - "несинхронная" операция.
        nextOrderNum = deleteInTargetDirsDeletedInSource(nextOrderNum)
        return nextOrderNum
    }

    private suspend fun deleteInTargetFilesDeletedInSource(nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { it.isFile}
            .filter { it.isDeletedInSource }
            .filter { it.notDeletedInTarget }
            .let { createInstructionsFor(it, SyncOperation6.DELETE_IN_TARGET, nextOrderNum) }
    }

    private suspend fun deleteInTargetDirsDeletedInSource(nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { it.isDir }
            .filter { it.isDeletedInSource }
            .filter { it.notDeletedInTarget }
            .let { createInstructionsFor(it, SyncOperation6.DELETE_IN_TARGET, nextOrderNum) }
    }


    private suspend fun processNeedToBeCopiedToTarget(initialOrderNum: Int): Int {
        var nextOrderNum = initialOrderNum

        nextOrderNum = reCreateInTargetDirsDeletedInTarget(nextOrderNum)

        nextOrderNum = copyToTargetFilesNewAndModifiedInSource(nextOrderNum)
        nextOrderNum = copyToTargetFilesNewModifiedDeletedInTarget(nextOrderNum)

        return nextOrderNum
    }


    private suspend fun reCreateInTargetDirsDeletedInTarget(nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { it.isDir }
            .filter { it.isDeletedInTarget }
            .filter { it.notDeletedInSource }
            .let { createInstructionsFor(it, SyncOperation6.COPY_FROM_SOURCE_TO_TARGET, nextOrderNum) }
    }

    private suspend fun copyToTargetFilesNewAndModifiedInSource(nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { it.isFile }
            .filter { it.isNewOrModifiedInSource }
            .filter { it.notDeletedInSource }
            .let { createInstructionsFor(it,SyncOperation6.COPY_FROM_SOURCE_TO_TARGET, nextOrderNum) }

    }

    private suspend fun copyToTargetFilesNewModifiedDeletedInTarget(nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { it.isFile }
            .filter { it.isNewModifiedDeletedInTarget }
            .filter { it.notDeletedInSource }
            .let { createInstructionsFor(it, SyncOperation6.COPY_FROM_SOURCE_TO_TARGET, nextOrderNum) }
    }


    private suspend fun createInstructionsFor(
        list: List<ComparisonState>,
        syncOperation: SyncOperation6,
        nextOrderNum: Int
    ): Int {
        var n = nextOrderNum
        list.forEach { comparisonState ->
            syncInstructionRepository6.add(
                SyncInstruction6.from(comparisonState, syncOperation, n++)
            )
        }
        return n
    }

    private suspend fun getAllBilateralComparisonStates(): Iterable<ComparisonState> {
        return comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter { it.isBilateral }
    }
}


@AssistedFactory
interface TwoPlaceInstructionGeneratorForSyncAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TwoPlaceInstructionGeneratorForSync
}