package com.github.aakumykov.sync_dir_to_cloud.extensions

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

fun SyncObject.absolutePathIn(dirPath: String): String {
    return (dirPath + CloudWriter.DS + relativeParentDirPath + CloudWriter.DS + name).stripMultiSlash()
}