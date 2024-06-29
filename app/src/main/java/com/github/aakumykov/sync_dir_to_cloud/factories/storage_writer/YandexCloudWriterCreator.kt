package com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.cloud_writer.YandexCloudWriter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class YandexCloudWriterCreator @AssistedInject constructor(
    @Assisted private val authToken: String
): CloudWriterFactory {
    override fun createCloudWriter(): CloudWriter {
        return YandexCloudWriter(authToken)
    }
}

@AssistedFactory
interface YandexCloudWriterAssistedFactory : CloudWriterAssistedFactory {
    override fun createCloudWriterFactory(authToken: String?): YandexCloudWriterCreator?
}