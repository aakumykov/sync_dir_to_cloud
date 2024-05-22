package com.github.aakumykov.sync_dir_to_cloud.factories.file_lister_creator

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

class FileListerCreatorFactory @Inject constructor(
    private val fileListerCreatorsMap: Map<StorageType, @JvmSuppressWildcards FileListerCreator>
) {
    fun createFileListerCreator(storageType: StorageType): FileListerCreator? {
        return fileListerCreatorsMap[storageType]
    }
}