package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ItemListsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val onlyInSourceItemsProcessorAssistedFactory: OnlyInSourceItemsProcessorAssistedFactory,
    private val onlyInTargetItemsProcessorAssistedFactory: OnlyInTargetItemsProcessorAssistedFactory,
    private val twoPlaceItemsMirrorProcessorAssistedFactory: TwoPlaceItemMirrorProcessorAssistedFactory,
    private val twoPlaceItemsSyncProcessorAssistedFactory: TwoPlaceItemSyncProcessorAssistedFactory,
) {
    suspend fun process() {

        onlyInSourceItemsProcessor.process()
        onlyInTargetItemsProcessor.process()

        when(syncTask.syncMode ?: SyncMode.SYNC) {
            SyncMode.SYNC -> twoPlaceSyncItemsProcessor.process()
            SyncMode.MIRROR -> twoPlaceMirrorItemsProcessor.process()
        }
    }

    private val onlyInSourceItemsProcessor by lazy {
        onlyInSourceItemsProcessorAssistedFactory.create(syncTask, executionId)
    }

    private val onlyInTargetItemsProcessor by lazy {
        onlyInTargetItemsProcessorAssistedFactory.create(syncTask, executionId)
    }

    private val twoPlaceMirrorItemsProcessor by lazy {
        twoPlaceItemsMirrorProcessorAssistedFactory.create(syncTask, executionId)
    }

    private val twoPlaceSyncItemsProcessor by lazy {
        twoPlaceItemsSyncProcessorAssistedFactory.create(syncTask, executionId)
    }
}


@AssistedFactory
interface ItemListsProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): ItemListsProcessor
}