package com.github.aakumykov.sync_dir_to_cloud.di.creators

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_reader.CloudReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

// TODO: классу место в вереве каталогов "di", так как он жёстко привязан к Dagger.
@Deprecated("Неудачное название, м.б. назвать Map, Holder, Registry?")
class CloudReaderLocator @Inject constructor(
    private val map: Map<StorageType, @JvmSuppressWildcards CloudReaderFactory>,
    private val authReader: CloudAuthReader,
){
    @Throws(NoSuchElementException::class)
    suspend fun getCloudReader(storageType: StorageType?, authToken: String): CloudReader {
        if (map.containsKey(storageType))
            return map.get(storageType)!!.createCloudReader(authToken)
        else
            throw NoSuchElementException("There is no CloudReaderFactory for storage type '${storageType}'")

    }

    @Throws(NoSuchElementException::class)
    suspend fun getCloudReaderWithAuthId(storageType: StorageType, authId: String): CloudReader {
        return getCloudReader(
            storageType,
            authReader.getCloudAuth(authId).authToken
        )
    }
}