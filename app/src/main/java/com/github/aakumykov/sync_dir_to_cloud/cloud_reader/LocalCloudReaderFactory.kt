package com.github.aakumykov.sync_dir_to_cloud.cloud_reader

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_reader.local_cloud_reader.LocalCloudReader
import javax.inject.Inject

class LocalCloudReaderFactory @Inject constructor() : CloudReaderFactory {
    override fun createCloudReader(authToken: String): CloudReader {
        return LocalCloudReader()
    }
}