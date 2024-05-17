package com.github.aakumykov.sync_dir_to_cloud.di.factories

import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

class RecursiveDirReaderFactory @Inject constructor(
    private val fileListerAssistedFactoriesMap: Map<StorageType, @JvmSuppressWildcards FileListerFactory>
) {
    fun create(storageType: StorageType, authToken: String): RecursiveDirReader? {
        return fileListerAssistedFactoriesMap[storageType]?.let { factory ->
            RecursiveDirReader(factory.createFileLister(authToken))
        }
    }
}