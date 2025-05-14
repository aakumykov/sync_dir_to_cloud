package com.github.aakumykov.sync_dir_to_cloud.extensions

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths

val SyncTask.currentSourceBackupsDir: String?
    get() = sourcePath?.plus(
        sourceBackupDirName?.plus(
            sourceExecutionBackupDirName
        )
    )

val SyncTask.currentTargetBackupsDir: String?
    get() = targetPath?.plus(
        targetBackupDirName?.plus(
            targetExecutionBackupDirName
        )
    )

val SyncTask.sourceBackupsDirPath: String?
    get() = sourceBackupDirName?.let { combineFSPaths(sourcePath!!, it) }


val SyncTask.targetBackupsDirPath: String?
    get() = targetBackupDirName?.let { combineFSPaths(targetPath!!, it) }


fun SyncTask.taskBackupDirNameFor(syncSide: SyncSide): String? = when(syncSide) {
    SyncSide.SOURCE -> sourceBackupDirName
    SyncSide.TARGET -> targetBackupDirName
}

fun SyncTask.executionBackupDirNameFor(syncSide: SyncSide): String? = when(syncSide) {
    SyncSide.SOURCE -> sourceExecutionBackupDirName
    SyncSide.TARGET -> targetExecutionBackupDirName
}

/*
val SyncTask.sourceBackupDirsPrepared: Boolean
    get() = sourceBackupDirName != null && sourceExecutionBackupDirName != null

*/
