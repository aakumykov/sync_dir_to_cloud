package com.github.aakumykov.sync_dir_to_cloud.rdr_factory

import com.github.aakumykov.file_lister.FileLister
import com.github.aakumykov.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.StorageType
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.interfaces.FileListerFactory
import javax.inject.Inject

class RecursiveDirReaderFactory @Inject constructor(
    private val fileListerFactoriesMap: Map<StorageType, FileListerFactory>
) {

    fun create(storageType: StorageType, cloudAuth: CloudAuth): RecursiveDirReader? {
        return fileListerFactoriesMap[storageType]?.create(cloudAuth)?.let {
            RecursiveDirReader(it)
        }
    }
}