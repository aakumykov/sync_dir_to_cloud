package com.github.aakumykov.sync_dir_to_cloud.factories.cloud_reader

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_reader.yandex_cloud_reader.YandexCloudReader
import javax.inject.Inject

class YandexDiskCloudReaderFactory @Inject constructor() : CloudReaderFactory {
    override fun createCloudReader(authToken: String?): CloudReader? {
        if (null == authToken)
            return null
        return YandexCloudReader(authToken)
    }
}