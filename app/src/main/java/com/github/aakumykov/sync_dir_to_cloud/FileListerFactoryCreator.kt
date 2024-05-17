package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.di.factories.FileListerFactory
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

class FileListerFactoryCreator @Inject constructor(
    private val map: Map<StorageType, @JvmSuppressWildcards FileListerFactory>
) {
    fun createFileListerFactory(storageType: StorageType): FileListerFactory? = map[storageType]
}