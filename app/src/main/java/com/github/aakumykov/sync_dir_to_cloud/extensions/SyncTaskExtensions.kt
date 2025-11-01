package com.github.aakumykov.sync_dir_to_cloud.extensions

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.functions.anyIsNull
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths

val SyncTask.executionBackupDirRelativePathInSource: String?
    get() = sourcePath?.let {
        if (anyIsNull(sourceTaskBackupsDirAbsolutePath, sourceExecutionBackupDirName)) null
        else combineFSPaths(sourceTaskBackupDirName!!, sourceExecutionBackupDirName!!)
    }


val SyncTask.executionBackupDirRelativePathInTarget: String?
    get() = targetPath?.let {
        if (anyIsNull(targetTaskBackupsDirAbsolutePath, targetExecutionBackupDirName)) null
        else combineFSPaths(targetTaskBackupDirName!!, targetExecutionBackupDirName!!)
    }


val SyncTask.sourceTaskBackupsDirAbsolutePath: String?
    get() = sourceTaskBackupDirName?.let { combineFSPaths(sourcePath!!, it) }


val SyncTask.targetTaskBackupsDirAbsolutePath: String?
    get() = targetTaskBackupDirName?.let { combineFSPaths(targetPath!!, it) }



val SyncTask.sourceExecutionBackupDirAbsolutePath: String?
    get() = sourceExecutionBackupDirName?.let { combineFSPaths(sourceTaskBackupsDirAbsolutePath!!, it) }


val SyncTask.targetExecutionBackupDirAbsolutePath: String?
    get() = targetExecutionBackupDirName?.let { combineFSPaths(targetTaskBackupsDirAbsolutePath!!, it) }


val SyncTask.isNotLocal: Boolean
    get() = StorageType.LOCAL != sourceStorageType!! || StorageType.LOCAL != targetStorageType!!

