package com.github.aakumykov.sync_dir_to_cloud.factories.cloud_writer

import com.github.aakumykov.cloud_writer.CloudWriter

interface CloudWriterFactory {
    fun create(authToken: String): CloudWriter
}