package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.source_reader.strategy

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.utils.calculateRelativeParentDirPath
import javax.inject.Inject

class SizeAndModificationTimeStrategy @Inject constructor(
    private val syncObjectReader: SyncObjectReader,
) : ChangesDetectionStrategy() {

    override suspend fun detectItemModification(
        taskId: String,
        sourcePath: String,
        newFsItem: FSItem
    ): ModificationState {

        val oldFSItem: FSItem? = syncObjectReader.getSyncObject(
            newFsItem.name,
            calculateRelativeParentDirPath(newFsItem, sourcePath),
            taskId
            // FIXME: это должно быть как-то инкапсулировано...
        )?.toFSItem(sourcePath)

        // Если такого элемента нет, то он получает статус "новый".
        if (null == oldFSItem)
            return ModificationState.NEW
        else
            // Если есть, и это каталог, то "не изменён", несмотря на возможное изменение размера
            // (чтобы не загружать повторно).
            if (oldFSItem.isDir)
                return ModificationState.UNCHANGED

        if (oldFSItem.size == newFsItem.size) {
            if (oldFSItem.mTime == newFsItem.mTime)
                return ModificationState.UNCHANGED
        }

        return ModificationState.MODIFIED
    }

    override suspend fun detectItemModification(existingSyncObject: SyncObject?, newFsItem: FSItem): ModificationState {

        // Если такого элемента нет, то он получает статус "новый".
        if (null == existingSyncObject)
            return ModificationState.NEW
        else
        // Если есть, и это каталог, то "не изменён", несмотря на возможное изменение размера
        // (чтобы не загружать повторно).
            if (existingSyncObject.isDir)
                return ModificationState.UNCHANGED

        if (existingSyncObject.size == newFsItem.size) {
            if (existingSyncObject.mTime == newFsItem.mTime)
                return ModificationState.UNCHANGED
        }

        return ModificationState.MODIFIED
    }
}