package com.github.aakumykov.sync_dir_to_cloud.aa_v2.reading_from_source

import android.util.Log
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.factories.recursive_dir_reader.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

class StorageToDatabaseLister @Inject constructor(
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectAdder: SyncObjectAdder,
    private val syncObjectUpdater: SyncObjectUpdater
) {
    fun listDir(path: String, cloudAuth: CloudAuth) {
        try {
            recursiveDirReaderFactory.create(
                cloudAuth.storageType,
                cloudAuth.authToken
            )
                ?.listDirRecursively(path)
                ?.forEach { fileListItem ->
                    addOrUpdateFileListItem(fileListItem)
                }
        } catch (e: Exception) {
            Log.e(TAG, ExceptionUtils.getErrorMessage(e), e);
        }
    }

    private fun addOrUpdateFileListItem(fileListItem: RecursiveDirReader.FileListItem) {

    }

    companion object {
        val TAG: String = StorageToDatabaseLister::class.java.simpleName
    }
}