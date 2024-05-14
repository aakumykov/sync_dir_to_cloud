package com.github.aakumykov.sync_dir_to_cloud.file_checker_creator

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

class CloudReaderCreator @Inject constructor(private val map: Map<StorageType, CloudReader>){
    fun createCloudReader(storageType: StorageType): CloudReader? = map[storageType]
}
