package com.github.aakumykov.sync_dir_to_cloud.factories.cloud_writer

import com.github.aakumykov.cloud_writer.YandexCloudWriter
import dagger.assisted.AssistedFactory

@AssistedFactory
interface YandexCloudWriterFactory : CloudWriterFactory {
    override fun create(authToken: String): YandexCloudWriter
}