package com.github.aakumykov.sync_dir_to_cloud.cloud_reader

import com.github.aakumykov.cloud_reader.CloudReader

interface CloudReaderFactory {
    fun createCloudReader(authToken: String): CloudReader
}