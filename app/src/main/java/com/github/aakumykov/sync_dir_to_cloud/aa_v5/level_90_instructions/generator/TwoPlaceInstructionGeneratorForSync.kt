package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_90_instructions.generator

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isModifiedInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isModifiedOrDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNewModifiedDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNewOrModifiedInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isUnchangedInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.notDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.notDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.PartsLabel
import com.github.aakumykov.sync_dir_to_cloud.repository.ComparisonStateRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TwoPlaceInstructionGeneratorForSync @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository: SyncInstructionRepository,
) {
    /**
     * @return Увеличенный порядковый номер
     */
    suspend fun generate(initialOrderNum: Int): Int {
        Log.d(TAG, "generate() called with: initialOrderNum = $initialOrderNum")

        var nextOrderNum = initialOrderNum

        // Бекаплю удалённые и изменённые файлы (если нужно).
        nextOrderNum = processFilesNeedToBeBackupedInTarget(nextOrderNum)

        // Удаляю удалённые файлы.
        nextOrderNum = processNeedToBeDeletedInTarget(nextOrderNum)

        // Копирую новое.
        nextOrderNum = processNeedToBeCopiedToTarget(nextOrderNum)

        return nextOrderNum
    }

    private suspend fun processFilesNeedToBeBackupedInTarget(nextOrderNum: Int): Int {
        return if (syncTask.withBackup) {
            getAllBilateralComparisonStates()
                .filter { it.isFile }
                .filter { it.isModifiedOrDeletedInSource || it.isModifiedInTarget }
                .filter { it.notDeletedInTarget }
                .let {
                    createInstructionsFor(it, SyncOperation.BACKUP_IN_TARGET, nextOrderNum)
                }
        } else nextOrderNum
    }


    private suspend fun processNeedToBeDeletedInTarget(initialOrderNum: Int): Int {
        var nextOrderNum = initialOrderNum

        // Сначала удаляю все файлы
        nextOrderNum = deleteFilesInTargetDeletedInSource(nextOrderNum)

        // Потом каталоги (которые к этой поре должны стать пустыми).
        // Ибо удаление непустого каталога в облаке - "несинхронная" операция.
        nextOrderNum = deleteDirsInTargetDeletedInSource(nextOrderNum)

        return nextOrderNum
    }

    private suspend fun deleteFilesInTargetDeletedInSource(nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { it.isFile}
            .filter { it.isDeletedInSource }
            .filter { it.notDeletedInTarget }
            .let {
                createInstructionsFor(
                    it,
                    deleteOrBackupInTarget(isDir = false),
                    nextOrderNum
                )
            }
    }

    private suspend fun deleteDirsInTargetDeletedInSource(nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { it.isDir }
            .filter { it.isDeletedInSource }
            .filter { it.notDeletedInTarget }
            .let {
                createInstructionsFor(
                    list = it,
                    deleteOrBackupInTarget(isDir = true),
                    nextOrderNum = nextOrderNum
                )
            }
    }

    private fun deleteOrBackupInTarget(isDir: Boolean): List<SyncOperation> {
        return buildList {
            if (syncTask.withBackup) {
                add(SyncOperation.BACKUP_IN_TARGET)
                // Бекап файла делается перемещением, что эквивалентно удалению,
                // поэтому собственно удаление требуется только каталогу.
                if (isDir)
                    add(SyncOperation.DELETE_IN_TARGET)
            }
            else add(SyncOperation.DELETE_IN_TARGET)
        }
    }


    private suspend fun processNeedToBeCopiedToTarget(initialOrderNum: Int): Int {
//        Log.d(TAG, "STORAGE_STATE, processNeedToBeCopiedToTarget()")

        var nextOrderNum = initialOrderNum

        nextOrderNum = reCreateInTargetDirsDeletedInTarget(nextOrderNum)

        nextOrderNum = copyToTargetFilesNewAndModifiedInSource(nextOrderNum)

        nextOrderNum = reCopyToTargetFilesNewModifiedDeletedInTarget(nextOrderNum)

        return nextOrderNum
    }


    private suspend fun reCreateInTargetDirsDeletedInTarget(nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { it.isDir }
            .filter { it.isDeletedInTarget }
            .filter { it.notDeletedInSource }
            .let { createInstructionsFor(it, SyncOperation.COPY_FROM_SOURCE_TO_TARGET, nextOrderNum) }
    }

    private suspend fun copyToTargetFilesNewAndModifiedInSource(nextOrderNum: Int): Int {
//        Log.d(TAG, "STORAGE_STATE, copyToTargetFilesNewAndModifiedInSource() called with: nextOrderNum = $nextOrderNum")
        return getAllBilateralComparisonStates()
            .let { list ->
                list.apply {
//                    Log.d(TAG, "STORAGE_STATE, getAllBilateralComparisonStates: ${this.joinToString(",") { it.toString() }}")
                }
            }
            .filter { it.isFile }
            .let { list ->
                list.apply {
//                    Log.d(TAG, "STORAGE_STATE, isFile: ${this.joinToString(",") { it.toString() }}")
                }
            }
            .filter { it.isNewOrModifiedInSource }
            .let { list ->
                list.apply {
//                    Log.d(TAG, "STORAGE_STATE, isNewOrModifiedInSource: ${this.joinToString(",") { it.toString() }}")
                }
            }
            .filter { it.notDeletedInSource }
            .let { list ->
                list.apply {
//                    Log.d(TAG, "STORAGE_STATE, notDeletedInSource: ${this.joinToString(",") { it.toString() }}")
                }
            }
            .let { createInstructionsFor(it,SyncOperation.COPY_FROM_SOURCE_TO_TARGET, nextOrderNum) }

    }

    private suspend fun reCopyToTargetFilesNewModifiedDeletedInTarget(nextOrderNum: Int): Int {
        return getAllBilateralComparisonStates()
            .filter { it.isFile }
            .filter { it.isNewModifiedDeletedInTarget }
            .filter { it.notDeletedInSource }
            .filter { it.isUnchangedInSource }
            .let { createInstructionsFor(it, SyncOperation.COPY_FROM_SOURCE_TO_TARGET, nextOrderNum) }
    }

    private suspend fun createInstructionsFor(
        list: List<ComparisonState>,
        syncOperationList: List<SyncOperation>,
        nextOrderNum: Int
    ): Int {
//        Log.d(TAG, "----- STORAGE_STATE createInstructionsFor(${syncOperationList.joinToString(",")}) -----")

        var n = nextOrderNum
        list.forEach { comparisonState ->
//            Log.d(TAG, comparisonState.toString())
////            Log.d(TAG, "STORAGE_STATE, ${comparisonState.toString()}")
            syncInstructionRepository.apply {
                syncOperationList.forEach { syncOperation ->
                    add(
                        SyncInstruction.from(
                            partsLabel = PartsLabel.ST,
                            comparisonState = comparisonState,
                            operation = syncOperation,
                            orderNum = n++
                        )
                    )
                }
            }
        }
//        Log.d("TAG", "STORAGE_STATE -----------------------------------------------------------------")
        return n
    }

    private suspend fun createInstructionsFor(
        list: List<ComparisonState>,
        syncOperation: SyncOperation,
        nextOrderNum: Int
    ): Int {
        return createInstructionsFor(
            list = list,
            syncOperationList = listOf(syncOperation),
            nextOrderNum = nextOrderNum
        )
    }

    private suspend fun getAllBilateralComparisonStates(): Iterable<ComparisonState> {
        return comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter { it.isBilateral }
    }

    companion object {
        val TAG: String = TwoPlaceInstructionGeneratorForSync::class.java.simpleName
    }
}


@AssistedFactory
interface TwoPlaceInstructionGeneratorForSyncAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TwoPlaceInstructionGeneratorForSync
}