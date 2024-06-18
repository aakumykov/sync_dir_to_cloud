package com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.cloud_writer.LocalCloudWriter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface LocalCloudWriterFactory : CloudWriterFactory {
    override fun create(
        @Assisted(CloudWriter.ARG_NAME_AUTH_TOKEN) authToken: String
    ): LocalCloudWriter
}