package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectBackuper5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectBackuperAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectDeleter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectDeleterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StoragePriority
import com.github.aakumykov.sync_dir_to_cloud.extensions.isSameWith
import com.github.aakumykov.sync_dir_to_cloud.repository.room.ComparisonStateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TwoPlaceItemSyncProcessor @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val comparisonStateRepository: ComparisonStateRepository,
    private val syncObjectCopier5AssistedFactory: SyncObjectCopierAssistedFactory5,
    private val syncObjectBackuper5AssistedFactory: SyncObjectBackuperAssistedFactory5,
    private val syncObjectDeleter5AssistedFactory: SyncObjectDeleterAssistedFactory5,
) {
    suspend fun p1(commonItems: Iterable<SyncObject>, allSourceItems: Iterable<SyncObject>, allTargetItems: Iterable<SyncObject>) {
        // Прежние в источнике
        processUnchangedInSourceAndUnchangedInTarget(commonItems, allSourceItems, allTargetItems)
        processUnchangedInSourceAndNewInTarget(commonItems, allSourceItems, allTargetItems)
        processUnchangedInSourceAndModifiedInTarget(commonItems, allSourceItems, allTargetItems)
        processUnchangedInSourceAndDeletedInTarget(commonItems, allSourceItems, allTargetItems)

        // Новые в источнике
        processNewInSourceAndUnchangedInTarget(commonItems, allSourceItems, allTargetItems)
        processNewInSourceAndNewInTarget(commonItems, allSourceItems, allTargetItems)
        processNewInSourceAndModifiedInTarget(commonItems, allSourceItems, allTargetItems)
        processNewInSourceAndDeletedInTarget(commonItems, allSourceItems, allTargetItems)

        // Изменившиеся в источнике
        processModifiedInSourceAndUnchangedInTarget(commonItems, allSourceItems, allTargetItems)
        processModifiedInSourceAndNewInTarget(commonItems, allSourceItems, allTargetItems)
        processModifiedInSourceAndModifiedInTarget(commonItems, allSourceItems, allTargetItems)
        processModifiedInSourceAndDeletedInTarget(commonItems, allSourceItems, allTargetItems)

        // Удалённые в источнике
        processDeletedInSourceAndUnchangedInTarget(commonItems, allSourceItems, allTargetItems)
        processDeletedInSourceAndNewInTarget(commonItems, allSourceItems, allTargetItems)
        processDeletedInSourceAndModifiedInTarget(commonItems, allSourceItems, allTargetItems)
        processDeletedInSourceAndDeletedInTarget(commonItems, allSourceItems, allTargetItems)
    }



    private fun processUnchangedInSourceAndUnchangedInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {
        // Ничего не делать
    }

    private suspend fun processUnchangedInSourceAndNewInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {
        /*comparisonStateRepository
            .getAllFor(syncTask.id, executionId)
            .filter { it.sourceObjectState }*/
    }

    private fun processUnchangedInSourceAndModifiedInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }

    private fun processUnchangedInSourceAndDeletedInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }


    private fun processNewInSourceAndUnchangedInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }

    private fun processNewInSourceAndNewInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }

    private fun processNewInSourceAndModifiedInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }

    private fun processNewInSourceAndDeletedInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }


    private fun processModifiedInSourceAndUnchangedInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }

    private fun processModifiedInSourceAndNewInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }

    private fun processModifiedInSourceAndModifiedInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }

    private fun processModifiedInSourceAndDeletedInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }


    private fun processDeletedInSourceAndUnchangedInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }

    private fun processDeletedInSourceAndNewInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }

    private fun processDeletedInSourceAndModifiedInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }

    private fun processDeletedInSourceAndDeletedInTarget(
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>
    ) {

    }


    suspend fun process(
        withBackup: Boolean,
        priority: StoragePriority,
        commonItems: Iterable<SyncObject>,
        allSourceItems: Iterable<SyncObject>,
        allTargetItems: Iterable<SyncObject>,
    ) {
//        val actions = mutableListOf<Action>()
        commonItems.forEach { commonItem ->
            val sourceItem = allSourceItems.first { commonItem.isSameWith(it) }
            val targetItem = allTargetItems.first { commonItem.isSameWith(it) }
            /*when(sourceItem.stateInStorage) {
                StateInStorage.UNCHANGED -> processUnchangedAnd(sourceItem, targetItem, withBackup, priority)
                StateInStorage.NEW -> processNewAnd(sourceItem, targetItem, withBackup, priority)
                StateInStorage.MODIFIED -> processModifiedAnd(sourceItem, targetItem, withBackup, priority)
                StateInStorage.DELETED -> processDeletedAnd(sourceItem, targetItem, withBackup, priority)
            }*/

        }
    }

    abstract class Action(val sourceItem: SyncObject, targetItem: SyncObject, val nextAction: Action? = null)
    class BackupAction(sourceItem: SyncObject,targetItem: SyncObject) : Action(sourceItem, targetItem)


    private fun processUnchangedAnd(
        sourceItem: SyncObject,
        targetItem: SyncObject,
        withBackup: Boolean,
        priority: StoragePriority
    ) {
        /*when(targetItem.stateInStorage) {
            StateInStorage.UNCHANGED -> processUnchangedAndUnchanged(sourceItem,targetItem,withBackup,priority)
            StateInStorage.NEW -> processUnchangedAndNew(sourceItem,targetItem,withBackup,priority)
            StateInStorage.MODIFIED -> processUnchangedAndModified(sourceItem,targetItem,withBackup,priority)
            StateInStorage.DELETED -> processUnchangedAndDeleted(sourceItem,targetItem,withBackup,priority)
        }*/
    }

    private fun processUnchangedAndUnchanged(
        sourceItem: SyncObject,
        targetItem: SyncObject,
        withBackup: Boolean,
        priority: StoragePriority
    ) {

    }

    private fun processNewAnd(
        sourceItem: SyncObject,
        targetItem: SyncObject,
        withBackup: Boolean,
        priority: StoragePriority
    ) {
        /*when(targetItem.stateInStorage) {
            StateInStorage.UNCHANGED -> processNewAndUnchanged(sourceItem,targetItem,withBackup,priority)
            StateInStorage.NEW -> processNewAndNew(sourceItem,targetItem,withBackup,priority)
            StateInStorage.MODIFIED -> processNewAndModified(sourceItem,targetItem,withBackup,priority)
            StateInStorage.DELETED -> processNewAndDeleted(sourceItem,targetItem,withBackup,priority)
        }*/
    }

    private fun processModifiedAnd(
        sourceItem: SyncObject,
        targetItem: SyncObject,
        withBackup: Boolean,
        priority: StoragePriority
    ) {
        /*when(targetItem.stateInStorage) {
            StateInStorage.UNCHANGED -> processModifiedAndModified(sourceItem,targetItem,withBackup,priority)
            StateInStorage.NEW -> processModifiedAndNew(sourceItem,targetItem,withBackup,priority)
            StateInStorage.MODIFIED -> processModifiedAndModified(sourceItem,targetItem,withBackup,priority)
            StateInStorage.DELETED -> processModifiedAndDeleted(sourceItem,targetItem,withBackup,priority)
        }*/
    }

    private fun processDeletedAnd(
        sourceItem: SyncObject,
        targetItem: SyncObject,
        withBackup: Boolean,
        priority: StoragePriority
    ) {
        /*when(targetItem.stateInStorage) {
            StateInStorage.UNCHANGED -> processDeletedAndUnchanged(sourceItem,targetItem,withBackup,priority)
            StateInStorage.NEW -> processDeletedAndNew(sourceItem,targetItem,withBackup,priority)
            StateInStorage.MODIFIED -> processDeletedAndModified(sourceItem,targetItem,withBackup,priority)
            StateInStorage.DELETED -> processDeletedAndDeleted(sourceItem,targetItem,withBackup,priority)
        }*/
    }




    private val syncObjectCopier5: SyncObjectCopier5 by lazy {
        syncObjectCopier5AssistedFactory.create(syncTask)
    }

    private val syncObjectBackuper5: SyncObjectBackuper5 by lazy {
        syncObjectBackuper5AssistedFactory.create(syncTask)
    }

    private val syncObjectDeleter5: SyncObjectDeleter5 by lazy {
        syncObjectDeleter5AssistedFactory.create(syncTask)
    }
}


@AssistedFactory
interface TwoPlaceSyncItemProcessorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): TwoPlaceItemSyncProcessor
}