package com.github.aakumykov.sync_dir_to_cloud.extensions

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

fun SyncObject.absolutePathIn(dirPath: String): String {
    return (dirPath + CloudWriter.DS + relativeParentDirPath + CloudWriter.DS + name).stripMultiSlash()
}

fun SyncObject.basePathIn(dirPath: String): String {
    return (dirPath + CloudWriter.DS + relativeParentDirPath).stripMultiSlash()
}

val SyncObject.relativePath: String
    get() = relativeParentDirPath + FSItem.DS + name


fun SyncObject.isSameWith(other: SyncObject): Boolean {
    return taskId == other.taskId &&
//            executionId == other.executionId &&
            name == other.name &&
            relativeParentDirPath == other.relativeParentDirPath
}