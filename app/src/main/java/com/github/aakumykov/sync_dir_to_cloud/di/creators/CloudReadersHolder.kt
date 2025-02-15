package com.github.aakumykov.sync_dir_to_cloud.di.creators

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_reader.CloudReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

// TODO: классу место в вереве каталогов "di", так как он жёстко привязан к Dagger.
class CloudReadersHolder @Inject constructor(
    private val map: Map<StorageType, @JvmSuppressWildcards CloudReaderFactory>
){
    @Throws(Exception::class)
    fun getCloudReader(storageType: StorageType?, authToken: String): CloudReader {
        if (map.containsKey(storageType))
            return map.get(storageType)!!.createCloudReader(authToken)
        else
            throw NoSuchElementException("There is no CloudReaderFactory for storage type '${storageType}'")
    }
}