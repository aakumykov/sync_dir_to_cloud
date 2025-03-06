package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StoragePriority
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.intersectBy
import com.github.aakumykov.sync_dir_to_cloud.extensions.subtractBy
import com.github.aakumykov.sync_dir_to_cloud.helpers.areObjectsTheSame
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ItemListsProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
//    private val syncObjectReader: SyncObjectReader,
    private val onlyInSourceItemsProcessorAssistedFactory: OnlyInSourceItemsProcessorAssistedFactory,
    private val onlyInTargetItemsProcessorAssistedFactory: OnlyInTargetItemsProcessorAssistedFactory,
    private val twoPlaceItemsProcessorAssistedFactory: TwoPlaceSyncItemProcessorAssistedFactory,
) {
    suspend fun process() {

/*
        val sourceObjectsList = syncObjectReader
            .getAllObjectsForTask(SyncSide.SOURCE, syncTask.id)
            .toMutableList()

        val targetObjectsList = syncObjectReader
            .getAllObjectsForTask(SyncSide.TARGET, syncTask.id)
            .toMutableList()

        val both = sourceObjectsList.intersectBy(targetObjectsList, ::areObjectsTheSame)
        val onlyInSource = sourceObjectsList.subtractBy(targetObjectsList, ::areObjectsTheSame)
        val onlyInTarget = targetObjectsList.subtractBy(sourceObjectsList, ::areObjectsTheSame)
*/

        onlyInSourceItemsProcessor.process()
//        onlyInTargetItemsProcessor.process(list = onlyInTarget, syncMode = syncTask.syncMode!!)

        when(syncTask.syncMode ?: SyncMode.SYNC) {
            SyncMode.SYNC -> twoPlaceSyncItemsProcessor.process()
            SyncMode.MIRROR -> {}
        }
    }

    private val onlyInSourceItemsProcessor by lazy {
        onlyInSourceItemsProcessorAssistedFactory.create(syncTask, executionId)
    }

    private val onlyInTargetItemsProcessor by lazy {
        onlyInTargetItemsProcessorAssistedFactory.create(syncTask)
    }

    private val twoPlaceSyncItemsProcessor by lazy {
        twoPlaceItemsProcessorAssistedFactory.create(syncTask, executionId)
    }
}


@AssistedFactory
interface ItemListsProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): ItemListsProcessor
}