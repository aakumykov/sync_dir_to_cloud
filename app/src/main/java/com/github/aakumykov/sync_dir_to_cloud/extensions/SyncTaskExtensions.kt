package com.github.aakumykov.sync_dir_to_cloud.extensions

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.functions.anyIsNull
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths

val SyncTask.executionBackupDirRelativePathInSource: String?
    get() = sourcePath?.let {
        if (anyIsNull(sourceTaskBackupsDirPath, sourceExecutionBackupDirName)) null
        else combineFSPaths(sourceTaskBackupDirName!!, sourceExecutionBackupDirName!!)
    }


val SyncTask.executionBackupDirRelativePathInTarget: String?
    get() = targetPath?.let {
        if (anyIsNull(targetTaskBackupsDirPath, targetExecutionBackupDirName)) null
        else combineFSPaths(targetTaskBackupDirName!!, targetExecutionBackupDirName!!)
    }


val SyncTask.sourceTaskBackupsDirPath: String?
    get() = sourceTaskBackupDirName?.let { combineFSPaths(sourcePath!!, it) }


val SyncTask.targetTaskBackupsDirPath: String?
    get() = targetTaskBackupDirName?.let { combineFSPaths(targetPath!!, it) }



val SyncTask.sourceExecutionBackupDirPath: String?
    get() = sourceExecutionBackupDirName?.let { combineFSPaths(sourceTaskBackupsDirPath!!, it) }


val SyncTask.targetExecutionBackupDirPath: String?
    get() = targetExecutionBackupDirName?.let { combineFSPaths(targetTaskBackupsDirPath!!, it) }


val SyncTask.isNotLocal: Boolean
    get() = StorageType.LOCAL != sourceStorageType!! || StorageType.LOCAL != targetStorageType!!

