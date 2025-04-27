package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Deprecated("Вывести из использования")
class SyncObjectRegistrator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val syncObjectAdder: SyncObjectAdder,
) {
    suspend fun registerNewObjectInSource(syncObject: SyncObject) {

        if (syncObject.syncSide != SyncSide.TARGET)
            throw IllegalArgumentException("SyncObject must have syncSide=${SyncSide.TARGET} property: $syncObject")

        SyncObject.createFromExisting(
            syncObject = syncObject,
            newExecutionId = executionId,
            newSyncSide = SyncSide.SOURCE,
            newStateInStorage = StateInStorage.NEW,
        ).also {
            syncObjectAdder.addSyncObject(it)
        }
    }

    suspend fun registerNewObjectInTarget(syncObject: SyncObject) {

        if (syncObject.syncSide != SyncSide.SOURCE)
            throw IllegalArgumentException("SyncObject must have syncSide='${SyncSide.SOURCE}': $syncObject")

        SyncObject.createFromExisting(
            syncObject = syncObject,
            newExecutionId = executionId,
            newSyncSide = SyncSide.TARGET,
            newStateInStorage = StateInStorage.NEW,
        ).also {
            syncObjectAdder.addSyncObject(it)
        }
    }

}


@AssistedFactory
interface SyncObjectRegistratorAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): SyncObjectRegistrator
}
