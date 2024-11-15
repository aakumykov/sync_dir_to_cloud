package com.github.aakumykov.sync_dir_to_cloud.aa_v2

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.utils.calculateRelativeParentDirPath
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class SyncObjectChecker @AssistedInject constructor(
    @Assisted private val taskId: String,
    @Assisted private val  relativeParentDirPath: String,
    private val syncObjectReader: SyncObjectReader,
) {
    suspend fun check(
        fileListItem: RecursiveDirReader.FileListItem,
        taskId: String,
        targetPath: String,
    ): CheckResult {

        val existingObject: SyncObject? = syncObjectReader.getSyncObject(
            taskId = taskId,
            name = fileListItem.name,
            relativeParentDirPath = relativeParentDirPath,
        )

        // Статус "DELETED" объекты в БД уже имеют перед проверкой.
        return if (existingObject == null)
            CheckResult.New(
                SyncObject.createAsNew(
                    taskId = taskId,
                    fsItem = fileListItem,
                    relativeParentDirPath = calculateRelativeParentDirPath(fileListItem, targetPath)
                )
            )
        else if (objectsAreTheSame(existingObject, fileListItem))
            CheckResult.Unchanged
        else
            CheckResult.Modified(
                SyncObject.createFromExisting(existingObject, fileListItem, StateInSource.MODIFIED)
            )
    }

    private fun objectsAreTheSame(existingSyncObject: SyncObject, readedFromStorageFSItem: FSItem): Boolean {
        return (existingSyncObject.size == readedFromStorageFSItem.size) &&
                (existingSyncObject.mTime == readedFromStorageFSItem.mTime)
    }

    sealed class CheckResult {
        class New(val syncObject: SyncObject) : CheckResult()
        class Modified(val syncObject: SyncObject) : CheckResult()
        data object Unchanged : CheckResult()
        data object Deleted : CheckResult()
    }
}