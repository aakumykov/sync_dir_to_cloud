package com.github.aakumykov.sync_dir_to_cloud.cloud_writer

interface CloudWriterFactory {
    fun create(authToken: String): com.github.aakumykov.cloud_writer.CloudWriter
}