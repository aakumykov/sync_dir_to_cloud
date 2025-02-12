package com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer

import com.github.aakumykov.cloud_writer.CloudWriter

interface CloudWriterFactory {
    fun createCloudWriter(): CloudWriter
}

interface CloudWriterAssistedFactory {
    fun createCloudWriterFactory(authToken: String): CloudWriterFactory
}