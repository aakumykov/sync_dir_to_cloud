package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.di.file_lister.assisted_factories.FileListerAssistedFactory
import javax.inject.Inject

class RecursiveDirReaderFactory @Inject constructor(
    private val fileListerAssistedFactoriesMap: Map<StorageType, @JvmSuppressWildcards FileListerAssistedFactory>
) {
    fun create(storageType: StorageType, authToken: String): RecursiveDirReader? {
        return fileListerAssistedFactoriesMap[storageType]?.let { factory ->
            RecursiveDirReader(factory.create(authToken))
        }
    }
}