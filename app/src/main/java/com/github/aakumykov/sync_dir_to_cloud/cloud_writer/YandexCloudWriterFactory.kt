package com.github.aakumykov.sync_dir_to_cloud.cloud_writer

import dagger.assisted.AssistedFactory

@AssistedFactory
interface YandexCloudWriterFactory : CloudWriterFactory {
    override fun create(authToken: String): com.github.aakumykov.cloud_writer.YandexCloudWriter
}