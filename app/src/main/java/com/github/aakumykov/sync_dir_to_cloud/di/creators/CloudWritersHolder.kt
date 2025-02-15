package com.github.aakumykov.sync_dir_to_cloud.di.creators

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterAssistedFactory
import javax.inject.Inject

/**
 * Фактически, это фабрика фабрик. Dagger2 собирает реализации CloudWriterFactory
 * в словарь по типам хранилищ. Потом фабрика, соответствующая типу хранилища,
 * создаёт объект CloudWriterGetter, основываясь на данных авторизации.
 */
class CloudWritersHolder @Inject constructor(
    private val map: Map<StorageType, @JvmSuppressWildcards CloudWriterAssistedFactory>
){
    @Throws(NoSuchElementException::class)
    fun getCloudWriter(storageType: StorageType?, authToken: String): CloudWriter {
        if (map.containsKey(storageType)) {
            return map[storageType]!!
                .createCloudWriterFactory(authToken)
                .createCloudWriter()
        } else {
            throw NoSuchElementException("Where is no CloudWriterAssistedFactory for storage type '$storageType'")
        }
    }
}