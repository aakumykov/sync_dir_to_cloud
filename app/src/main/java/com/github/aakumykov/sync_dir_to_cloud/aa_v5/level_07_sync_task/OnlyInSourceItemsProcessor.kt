package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstructionRepository6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class OnlyInSourceItemsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncInstructionRepository6: SyncInstructionRepository6,
){
    suspend fun process() {
        processUnchangedNewModifiedDirs()
        processUnchangedNewModifiedFiles()
    }

    private fun processUnchangedNewModifiedDirs() {
        /*getOnlyInSourceStates()
            .filter { }*/
    }

    private fun processUnchangedNewModifiedFiles() {

    }

    private suspend fun getOnlyInSourceStates(): Iterable<ComparisonState> {
        return comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter {
                null != it.sourceObjectState &&
                        null == it.targetObjectState
            }
    }
}



@AssistedFactory
interface OnlyInSourceItemsProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): OnlyInSourceItemsProcessor
}