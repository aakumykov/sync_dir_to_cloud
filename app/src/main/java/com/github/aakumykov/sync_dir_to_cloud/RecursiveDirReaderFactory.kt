package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.recursive_dir_reader.RecursiveDirReader
import javax.inject.Inject
import kotlin.jvm.Throws

class RecursiveDirReaderFactory @Inject constructor() {

    // TODO: убрать null-able, добавить исключение...
//    @Throws
    fun create(storageType: StorageType, authToken: String): RecursiveDirReader? {
        return null
    }
}