package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.Side
import com.github.aakumykov.sync_dir_to_cloud.extensions.theSameWith
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import javax.inject.Inject

/*
"Таблица истинности"

*/
class SourceWithTargetComparator @Inject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectAdder: SyncObjectAdder,
){
    suspend fun compare(taskId: String,
                        executionId: String,
                        comparitionStrategy: ComparisionStrategy
    ) {
        val sourceItems: List<SyncObject> = syncObjectReader
            .getAllObjectsForTask(Side.SOURCE, taskId, executionId)

        val targetItems: List<SyncObject> = syncObjectReader
            .getAllObjectsForTask(Side.SOURCE, taskId, executionId)

        for (sourceItem in sourceItems) {
            val processingSteps = comparitionStrategy.compare(
                sourceItem.stateInStorage,
                targetItems.firstOrNull { it.theSameWith(sourceItem) }?.stateInStorage
            )
        }
    }

}
