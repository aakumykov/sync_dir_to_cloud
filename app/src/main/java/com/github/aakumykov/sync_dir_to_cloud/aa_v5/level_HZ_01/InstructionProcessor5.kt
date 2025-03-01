package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_HZ_01

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectBackuperAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectRenamerAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository5
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InstructionProcessor5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val instructionRepository5: SyncInstructionRepository5,
    private val syncObjectCopierAssistedFactory5: SyncObjectCopierAssistedFactory5,
    private val syncObjectDeleterAssistedFactory5: SyncObjectDeleterAssistedFactory5,
    private val syncObjectRenamerAssistedFactory5: SyncObjectRenamerAssistedFactory5,
    private val syncObjectBackuperAssistedFactory5: SyncObjectBackuperAssistedFactory5,
) {
    fun processSyncInstructions(executionId: String) {
        
    }
}


@AssistedFactory
interface InstructionProcessorAssistedFactory5 {
    fun create(syncTask: SyncTask): InstructionProcessor5
}