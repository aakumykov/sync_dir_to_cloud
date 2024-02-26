package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectChanges
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.utils.calculateRelativeParentDirPath
import javax.inject.Inject

class SyncObjectChangesDetector @Inject constructor(
    private val syncObjectReader: SyncObjectReader,
) {
    suspend fun detectSyncObjectChanges(
        taskId: String,
        fsItem: FSItem,
        sourcePath: String,
        changesDetectionStrategy: ChangesDetectionStrategy
    ): SyncObjectChanges {

        val relativeParentDirPath = calculateRelativeParentDirPath(fsItem, sourcePath)

        val existingObject: SyncObject? = syncObjectReader.getSyncObject(fsItem.name, relativeParentDirPath, taskId)

        return if (null != existingObject)
            SyncObjectChanges.create(
                existingObject.id,
                fsItem,
                changesDetectionStrategy.detectItemModification(existingObject, fsItem)
            )
        else
            SyncObjectChanges.createAsNew(
                SyncObject.create(taskId, fsItem, relativeParentDirPath, ModificationState.NEW)
            )
    }
}
