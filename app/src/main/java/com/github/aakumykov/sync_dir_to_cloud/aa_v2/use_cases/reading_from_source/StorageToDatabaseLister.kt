package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.reading_from_source

import android.util.Log
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.factories.recursive_dir_reader.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.utils.calculateRelativeParentDirPath
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

class StorageToDatabaseLister @Inject constructor(
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectAdder: SyncObjectAdder,
    private val syncObjectUpdater: SyncObjectUpdater,
) {
    @Throws(IllegalArgumentException::class)
    suspend fun readFromPath(pathReadingFrom: String?, cloudAuth: CloudAuth?, taskId: String) {

        if (null == pathReadingFrom)
            throw IllegalArgumentException("path argument is null")

        if (null == cloudAuth)
            throw IllegalArgumentException("cloudAuth argument is null")

        try {
            recursiveDirReaderFactory.create(cloudAuth.storageType, cloudAuth.authToken)
                ?.listDirRecursively(
                    path = pathReadingFrom,
                    foldersFirst = true
                )
                ?.forEach { fileListItem ->
                    addOrUpdateFileListItem(fileListItem, taskId, pathReadingFrom)
                }
        } catch (e: Exception) {
            Log.e(TAG, ExceptionUtils.getErrorMessage(e), e);
        }
    }

    // TODO: разобраться с определением статуса изменённости объекта
    private suspend fun addOrUpdateFileListItem(
        fileListItem: RecursiveDirReader.FileListItem,
        taskId: String,
        pathReadingFrom: String
    ) {
        val existingObject = syncObjectReader.getSyncObject(
            taskId,
            fileListItem.name,
            fileListItem.parentPath)

        if (null == existingObject) {
            syncObjectAdder.addSyncObject(
                SyncObject.createAsNew(
                    taskId,
                    fileListItem,
                    calculateRelativeParentDirPath(fileListItem, pathReadingFrom)
                )
            )
        }
        else {
            syncObjectUpdater.updateSyncObject(
                SyncObject.createFromExisting(existingObject, fileListItem, ModificationState.MODIFIED)
            )
        }
    }

    companion object {
        val TAG: String = StorageToDatabaseLister::class.java.simpleName
    }
}