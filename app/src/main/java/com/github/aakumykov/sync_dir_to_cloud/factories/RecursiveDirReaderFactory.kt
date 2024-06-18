package com.github.aakumykov.sync_dir_to_cloud.factories

import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.factories.file_lister.FileListerCreatorFactory
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

class RecursiveDirReaderFactory @Inject constructor(
    private val fileListerCreatorFactory: FileListerCreatorFactory
) {
    fun create(storageType: StorageType, authToken: String): RecursiveDirReader? {

        return fileListerCreatorFactory
            .createFileListerCreator(storageType)
            ?.createFileLister(authToken)
            ?.let {
                RecursiveDirReader(it)
            }
    }
}