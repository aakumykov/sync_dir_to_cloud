package com.github.aakumykov.sync_dir_to_cloud.di.creators

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_reader.CloudReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

// TODO: классу место в вереве каталогов "di", так как он жёстко привязан к Dagger.
class CloudReaderGetter @Inject constructor(
    private val map: Map<StorageType, @JvmSuppressWildcards CloudReaderFactory>
){
    fun getCloudReader(storageType: StorageType?, authToken: String?): CloudReader? {
        return map[storageType]?.createCloudReader(authToken)
    }
}