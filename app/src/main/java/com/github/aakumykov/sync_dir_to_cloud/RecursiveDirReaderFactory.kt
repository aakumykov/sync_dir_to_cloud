package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.di.file_lister.assisted_factories.FileListerAssistedFactory
import javax.inject.Inject
import kotlin.jvm.Throws

class RecursiveDirReaderFactory @Inject constructor(
    private val fileListerAssistedFactoriesMap: Map<StorageType, @JvmSuppressWildcards FileListerAssistedFactory>
) {

    // TODO: убрать null-able, добавить исключение...
//    @Throws
    fun create(storageType: StorageType, authToken: String): RecursiveDirReader? {
        return null
    }
}