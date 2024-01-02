package com.github.aakumykov.sync_dir_to_cloud.cloud_writer

import com.github.aakumykov.cloud_writer.LocalCloudWriter
import dagger.assisted.AssistedFactory

@AssistedFactory
interface LocalCloudWriterFactory :
    CloudWriterFactory {
    override fun create(authToken: String): LocalCloudWriter
}