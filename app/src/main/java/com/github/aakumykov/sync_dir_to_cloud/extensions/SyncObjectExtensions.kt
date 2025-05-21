package com.github.aakumykov.sync_dir_to_cloud.extensions

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.absolutePathOfSide

fun SyncObject.absolutePathIn(dirPath: String): String {
    return (dirPath + CloudWriter.DS + relativeParentDirPath + CloudWriter.DS + name)
        .stripMultiSlash()
}

/**
 * Id одного объекта в источнике и приёмнике различаются.
 * Поэтому объект по своему id однозначно приналдежит
 * одной из сторон.
 */
fun SyncObject.absolutePathIn(syncTask: SyncTask): String {
    return absolutePathIn(syncTask.absolutePathOfSide(syncSide))
}

fun SyncObject.absolutePathInWithNewName(dirPath: String, newName: String): String {
    return (dirPath + CloudWriter.DS + relativeParentDirPath + CloudWriter.DS + newName)
        .stripMultiSlash()
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