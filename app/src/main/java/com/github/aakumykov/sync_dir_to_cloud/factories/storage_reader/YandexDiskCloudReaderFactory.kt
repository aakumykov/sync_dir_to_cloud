package com.github.aakumykov.sync_dir_to_cloud.factories.storage_reader

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.yandex_disk_cloud_reader.YandexDiskCloudReader
import javax.inject.Inject

class YandexDiskCloudReaderFactory @Inject constructor() : CloudReaderFactory {
    override fun createCloudReader(authToken: String): CloudReader {
        return YandexDiskCloudReader(authToken)
    }
}